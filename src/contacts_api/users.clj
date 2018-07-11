(ns contacts-api.users
  (:require [contacts-api.db.postgresql :refer [db def-db-fns]]
            [contacts-api.response :refer [one many error]]
            [contacts-api.validation :refer [humanize-error non-empty-str valid-phone? validate]]
            [contacts-api.sessions :refer [sha-256]]
            [clojure.tools.logging :as log]
            [schema.core :as sc]))

(def ^:private users-fmt {:username non-empty-str
                          :password non-empty-str
                          (sc/optional-key :salt) non-empty-str})

(def ^:private default-salt-value "salt")

(def-db-fns "users.sql")

(defn create [{{:keys [username password salt] :as body} :body}]
  (let [[_ err] (validate users-fmt body)]
    (if-not err
      (let [salt (if salt salt default-salt-value)
            [_ err] (insert-user db (assoc body :password (sha-256 password salt) :salt salt))]
        (if-not err
          (one :created {})
          (let [err-msg "Problem creating user"]
            (log/error err err-msg)
            (error :unprocessable-entity err-msg))))
      (error :unprocessable-entity (humanize-error err)))))
