(ns dantheobserver.parrot-api.app
  (:require [dantheobserver.parrot-api.request :as request]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [response]]
            [ring.adapter.jetty :as jetty]))

(def db (atom {:api1 {"testing" {:hello "world"}}}))
(def port 3030)

(defn handler [{:keys [uri] :as req}]
  (if (= "/favicon.ico" uri)
    {:status 404}
    (let [req-data (request/request-data db req)]
      (println req-data)
      (response req-data))))

(def app (-> handler
             wrap-json-response))

(defn run-server []
  (jetty/run-jetty app {:port port}))

(defn run-dev-server []
  (jetty/run-jetty (wrap-reload app) {:port port}))
