(ns contacts-api.contacts
  (:require [contacts-api.db.postgresql :refer [db def-db-fns]]
            [contacts-api.response :refer [one many error]]
            [contacts-api.validation :refer [humanize-error non-empty-str valid-phone? validate]]
            [contacts-api.db.postgresql :refer [uuid]]
            [clojure.tools.logging :as log]
            [schema.core :as sc]))

(def ^:private contacts-fmt {:first_name non-empty-str
                             :last_name non-empty-str})

(def ^:private contacts-update-fmt {(sc/optional-key :first_name) non-empty-str
                                    (sc/optional-key :last_name) non-empty-str})

(def ^:private entries-fmt {:phone non-empty-str
                            (sc/optional-key :region) non-empty-str})

(def-db-fns "contacts.sql")

(defn- error-message [msg err]
  (let [err-msg msg]
    (log/error err err-msg)
    (error :unprocessable-entity err-msg)))

(defn list-all [{{:keys [login]} :session}]
  (let [[contacts err] (select-all-contacts-by-user db {:owner login})]
    (if-not err
      (if (seq contacts)
        (let [grouped-contacts (group-by :id contacts)
              processed-contacts (map #(let [e (key %)
                                             v (val %)
                                             c (select-keys (first v) [:id :first_name :last_name])
                                             p (filter some? (map :phone v))]
                                         (assoc c :phones p)) grouped-contacts)]

          (many :ok processed-contacts))
        (many :ok []))
      (error-message (str "Problem retrieving contacts for user" login) err))))

(defn create [{body :body {:keys [login]} :session}]
  (let [[_ err] (validate contacts-fmt body)]
    (if-not err
      (let [id (uuid)
            [_ err] (insert-contact db (assoc body :id id :owner login))]
        (if-not err
          (one :created {:id id})
          (let [err-msg (str "Problem creating contact for user" login)]
            (log/error err err-msg)
            (error :unprocessable-entity err-msg))))
      (error :unprocessable-entity (humanize-error err)))))

(defn update-name [{body :body {:keys [login]} :session {:keys [id]} :params}]
  (let [[_ err] (validate contacts-update-fmt body)]
    (if-not err
      (let [[_ err] (update-contact db (assoc body :id id :owner login))]
        (if-not err
          (one :ok {:id id})
          (error-message (str "Problem updating contact for user" login) err)))
      (error :unprocessable-entity (humanize-error err)))))

(defn delete [{{:keys [login]} :session {:keys [id]} :params}]
  (let [[_ err] (delete-contact db {:id id :owner login})]
    (if-not err
      (one :no-content {:api_message (str "Contact deleted: " id)})
      (error-message (str "Problem deleting contact " id) err))))

(defn add-phone [{{:keys [phone region] :as body} :body {:keys [id]} :params}]
  (let [[_ err] (validate entries-fmt body)
        phone-valid? (if region (valid-phone? phone region) true) ;;for this to work properly, the api documentation would need to be updated to allow a region value (optional for now)
        body (cond-> body
               (not region) (assoc :region "def"))]
    (if (and (not err) phone-valid?)
      (let [[_ err] (insert-entry db (assoc body :c_id id))]
        (if-not err
          (one :created {:id id})
          (error-message (str "Problem inserting entry for contact " id) err)))
      (error :unprocessable-entity (or (humanize-error err) (str "Phone " phone " not valid for region " region))))))
