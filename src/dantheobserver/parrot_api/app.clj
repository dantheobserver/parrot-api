(ns dantheobserver.parrot-api.app
  (:require [dantheobserver.parrot-api.response :as response]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [response]]))

(def db (atom {:api1 {"testing" {:hello "world"}}}))
(def config {:api1 "https://parrot-test.free.beeceptor.com"})
(def port 3030)

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

(defn run-dev-server []
  (println "---Starting dev server---")
  (jetty/run-jetty (wrap-reload app) {:port port :join? false}))

(defn -main [& args]
  (let [{hostname 0
         port 1
         :or {hostname "127.0.0.1"
              port "3030"}} (vec args)]
    (println "Running server on host: " hostname " port: 3030")
    (jetty/run-jetty app {:port 3030})))

