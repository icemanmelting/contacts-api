(ns contacts-api.contacts
  (:require [contacts-api.db.postgresql :refer [db def-db-fns]]
            [contacts-api.response :refer [one many error]]
            [contacts-api.validation :refer [humanize-error non-empty-str validate]]
            [clojure.tools.logging :as log]))

(def-db-fns "contacts.sql")

(defn list-all [{{:keys [login]} :session}]
  (let [[contacts err] (select-all-contacts-by-user db {:owner login})]
    (if-not err
      (if (seq contacts)
        (let [grouped-contacts (group-by :id contacts)
              processed-contacts (map #(let [e (key %)
                                             v (val %)
                                             c (select-keys (first v) [:id :first_name :last_name])
                                             p (map :phone v)]
                                         (assoc c :phones p)) grouped-contacts)]

          (many :ok processed-contacts))
        (many :ok []))
      (let [err-msg (str "Problem retrieving contacts for user" login)]
        (log/error err err-msg)
        (error :unprocessable-entity err-msg)))))

(defn create [req])

(defn update-name [req])

(defn add-phone [req])

(defn delete [req])

