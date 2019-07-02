(ns dantheobserver.parrot-api.app
  (:require [dantheobserver.parrot-api.response :as response]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [dantheobserver.parrot-api.common :refer [with-args]]))

(def db (atom {:api1 {"testing" {:hello "world"}}}))
(def config {:api1 "https://parrot-test.free.beeceptor.com"})

(defn handler [{:keys [uri] :as req}]
  (if (= "/favicon.ico" uri)
    {:status 404}
    (let [res-data (-> (response/stored-data db req))]
      ;; TODO: Check if res-data is present in cache
      ;; TODO: pull current endpoint from config and call with same req uri
      ;; TODO: store result in cache db
      (response res-data))))

(def app (-> handler
             wrap-json-response))

(defn -main [& args]
  (with-args args
    [hostname "127.0.0.1"
     port 3030]
    (println "Running server on host:" hostname " port:" port)
    (jetty/run-jetty app {:host hostname :port port})))
