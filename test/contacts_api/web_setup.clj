(ns contacts-api.web-setup
  (:require [contacts-api.core :refer [app]]
            [ring.mock.request :as mock]
            [cheshire.core :as json]))

(def resp (atom nil))

(def ^:private req-fn (atom nil))

(defn req [method uri body]
  (-> (mock/request method uri)
      (mock/content-type "application/json; charset=utf-8")
      (mock/body (json/generate-string body))))

(defn auth-req [method uri body]
  (mock/header (req method uri body) "Token" "00000000-0000-0000-0000-000000000000"))

(defn set-authorized-requests! []
  (reset! req-fn auth-req))

(defn set-unauthorized-requests! []
  (reset! req-fn req))

(defn web-run
  ([req-fn method uri body] (reset! resp (app (req-fn method uri body))))
  ([method uri body] (web-run @req-fn method uri body))
  ([method uri] (web-run method uri nil)))

(defn extract-body []
  (json/decode (:body @resp) keyword))

