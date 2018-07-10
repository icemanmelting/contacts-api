(ns contacts-api.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.cors :as cors]
            [ring.middleware.json :as json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [contacts-api.response :refer [error render response]]
            [contacts-api.sessions :refer [authorize] :as sessions]
            [contacts-api.contacts :as contacts]
            [contacts-api.users :as users])
  (:gen-class))

(defn- wrap-json-body [h]
  (json/wrap-json-body h {:keywords? true
                          :malformed-response (response (error :unprocessable-entity "Wrong JSON format"))}))

(defn- wrap-cors [h]
  (cors/wrap-cors h
                  :access-control-allow-origin #".+"
                  :access-control-allow-methods [:get :put :post :delete :options]))

(defroutes contacts-routes
  (GET "/contacts" [] (render contacts/list-all))
  (POST "/contacts" [] (render contacts/create))
  (POST "/contacts/:id" [] (render contacts/update-name))
  (DELETE "/contacts/:id" [] (render contacts/delete))
  (POST "/contacts:id/entries" [] (render contacts/add-phone)))

(defroutes users-routes
  (POST "/user" [] (render users/create)))


(defroutes session-routes
           (POST "/user/sessions" [] (render sessions/create))
           (GET "/user/sessions" [] (authorize (render sessions/find-one)))
           (DELETE "/user/sessions" [] (authorize (render sessions/destroy))))

(defroutes app
           (-> (routes session-routes users-routes (authorize contacts-routes)) wrap-json-body wrap-json-response wrap-cors))

(defn -main []
  (run-server app {:port 8080})
  (println "Listening on port 8080"))
