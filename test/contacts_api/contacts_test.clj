(ns contacts-api.contacts-test
  (:require [clojure.test :refer :all]
            [hugsql.core :refer [db-run]]
            [contacts-api.db.postgresql :refer [def-db-fns db uuid]]
            [contacts-api.web-setup :refer :all]))

(def ^:private id1 (uuid))

(def ^:private id2 (uuid))

(def ^:private phone-number "916278390")

(def ^:private owner "foo@bar.com")

(def ^:private expected-results
  [{:id (str id2)
    :first_name "Charles"
    :last_name "Montaineer"
    :phones []}
   {:id (str id1)
    :first_name "Fabio"
    :last_name "Francisco"
    :phones []}])

(def ^:private expected-id-body {:id (str id1)})

(def-db-fns "contacts.sql")

(defn- new-contact [id owner first-name last-name]
  (let [contact {:id id
                 :owner owner
                 :first_name first-name
                 :last_name last-name}]
    (insert-contact db contact)))

(defn- setup-session []
  (db-run db (str "INSERT INTO users (login, password, salt) VALUES"
                  "('" owner "', '3bb95188c01763e81875ce9644f496a4e3f98d9eb181c5f128ba32a01b62b6de', 'bar');"))
  (db-run db (str "INSERT INTO sessions (id, login, seen) VALUES"
                  "('00000000-0000-0000-0000-000000000000', '" owner "', now());")))

(defn- all-contacts-by-owner [owner]
  (first (select-all-contacts-by-user db {:owner owner})))

(defn- clear-pg []
  (db-run db "TRUNCATE users CASCADE"))

(defn- populate-pg []
  (setup-session)
  (new-contact id1 owner "Fabio" "Francisco")
  (new-contact id2 owner "Charles" "Montaineer"))

(defn- contact-fixture [f]
  (clear-pg)
  (populate-pg)
  (f))

(use-fixtures :each contact-fixture)

(deftest test-contact-retrieval

  (testing "contacts retrieval"

    (do (set-authorized-requests!)
        (web-run :get "/contacts"))

    (are [x y] (= x y)
               200 (:status @resp)
               (sort-by :id expected-results) (sort-by :id (extract-body))))

  (testing "unauthorized request"

    (do (set-unauthorized-requests!)
        (web-run :get "/contacts"))

    (let [status (-> @resp :status)]
      (is (= 403 status)))))

(deftest test-contact-creation

  (let [first-name "John"
        last-name "Doe"]

    (testing "contact-creation"

      (do (set-authorized-requests!)
          (web-run :post "/contacts" {:first_name first-name
                                      :last_name last-name}))

      (is (= 201 (:status @resp)))

      (let [all-contacts (all-contacts-by-owner owner)]

        (is (= 1 (count (filter #(and (= first-name (:first_name %))
                                      (= last-name (:last_name %))) all-contacts))))))

    (testing "missing body params"

      (do (set-authorized-requests!)
          (web-run :post "/contacts" {:first_name first-name}))

      (is (= 422 (:status @resp))))

    (testing "unauthorized request"

      (do (set-unauthorized-requests!)
          (web-run :post "/contacts" {:first_name first-name
                                      :last_name last-name}))

      (let [status (-> @resp :status)]
        (is (= 403 status))))))

(deftest test-contact-update

  (let [first-name "Jane"
        last-name "Doe"]

    (testing "contact update"

      (do (set-authorized-requests!)
          (web-run :put (str "/contacts/" id1) {:first_name first-name
                                                :last_name last-name}))

      (is (= 200 (:status @resp)))

      (is (= expected-id-body (extract-body)))

      (let [all-contacts (all-contacts-by-owner owner)]

        (is (= 1 (count (filter #(and (= first-name (:first_name %))
                                      (= last-name (:last_name %))) all-contacts))))))

    (testing "unauthorized request"

      (do (set-unauthorized-requests!)
          (web-run :put (str "/contacts/" id1) {:first_name first-name
                                                :last_name last-name}))

      (let [status (-> @resp :status)]
        (is (= 403 status))))))

(deftest test-contact-deletion

  (testing "contact deletion"

    (do (set-authorized-requests!)
        (web-run :delete (str "/contacts/" id1)))

    (are [x y] (= x y)
               204 (:status @resp))

    (is (= 0 (count (filter #(= id1 (:id %)) (all-contacts-by-owner owner))))))

  (testing "unauthorized request"

    (do (set-unauthorized-requests!)
        (web-run :delete (str "/contacts/" id1)))

    (let [status (-> @resp :status)]
      (is (= 403 status)))))

(deftest test-entry-creation

  (testing "entry creation"

    (do (set-authorized-requests!)
        (web-run :post (str "/contacts/" id1 "/entries") {:phone phone-number}))

    (is (= 201 (:status @resp)))

    (let [all-contacts (all-contacts-by-owner owner)
          id1-contact (-> (group-by :id all-contacts) (get id1) first)]
      (is (= phone-number (:phone id1-contact)))))

  (testing "missing body params"

    (do (set-authorized-requests!)
        (web-run :post (str "/contacts/" id1 "/entries") {}))

    (is (= 422 (:status @resp))))

  (testing "unauthorized request"

    (do (set-unauthorized-requests!)
        (web-run :post (str "/contacts/" id1 "/entries") {:phone phone-number}))

    (let [status (-> @resp :status)]
      (is (= 403 status)))))
