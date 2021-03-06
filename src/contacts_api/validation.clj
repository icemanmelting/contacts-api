(ns contacts-api.validation
  (:require [schema.core :as sc]
            [clojure.string :as cs])
  (:import (java.util.regex Pattern)
           (com.google.i18n.phonenumbers NumberParseException PhoneNumberUtil)))

(def non-empty-str (sc/constrained sc/Str not-empty))

(def uuid-str (sc/constrained sc/Str (comp not nil? (partial re-matches #"[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"))))

(defn valid-phone? [value code]
  (let [ph (PhoneNumberUtil/getInstance)]
    (when-let [parsed (try
                        (.parse ph value code)
                        (catch NumberParseException _))]
      (.isValidNumber ph parsed))))

(defn validate [schema data]
  (try
    [(sc/validate schema data) nil]
    (catch Exception e [nil e])))

(defn humanize-error [e]
  (when e
    (cs/replace (.getMessage e) #":(\w+)" "\"$1\"")))
