(ns contacts-api.users-test
  (:require [clojure.test :refer :all]
            [contacts-api.web-setup :refer :all]
            [contacts-api.sessions :refer [sha-256]]
            [hugsql.core :refer [db-run]]
            [contacts-api.db.postgresql :refer [db]]))

(def ^:private user-name "foo@bar.ez.com")

(def ^:private raw-password "reallyeasy")

(defn- retrieve-user []
  (-> (db-run db (str "SELECT * FROM users WHERE login='" user-name "';")) first first))

(defn- clear-pg []
  (db-run db "TRUNCATE users CASCADE"))

(defn- user-fixture [f]
  (clear-pg)
  (f))

(use-fixtures :each user-fixture)

(deftest test-user-creation

  (testing "user creation"

    (web-run :post (str "/user") {:username user-name
                                  :password raw-password})

    (let [{:keys [login password]} (retrieve-user)]

      (are [x y] (= x y)
                 201 (:status @resp)
                 user-name login
                 (sha-256 raw-password "salt") password)))

  (testing "missing body params"

    (web-run :post (str "/user") {:username user-name})

    (is (= 422 (:status @resp)))))
