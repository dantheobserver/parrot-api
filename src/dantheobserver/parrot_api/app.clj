(ns dantheobserver.parrot-api.app
  (:require [dantheobserver.parrot-api.request :as request]
            [ring.middleware.json :as json]
            [ring.util.response :refer [response]]
            [ring.adapter.jetty :as jetty]))

(defn handler [req]
  (println req)
  (response {:testing 123}))

(def app (-> handler
             json/wrap-json-response))

(jetty/run-jetty app {:port 3030})
