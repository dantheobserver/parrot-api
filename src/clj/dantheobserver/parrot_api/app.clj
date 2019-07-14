(ns dantheobserver.parrot-api.app
  (:require [dantheobserver.parrot-api.common :refer [fill]]
            [dantheobserver.parrot-api.config :as config]
            [dantheobserver.parrot-api.response :as response]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]))

;; Seed data
(defmethod ig/init-key :database.sql/datasource
  [_ connection-data]
  (jdbc/get-datasource connection-data))

(defmethod ig/init-key ::handler
  [_ {:keys [data-store]}]
  (wrap-json-response
    (fn [{:keys [uri] :as req}]
      (if (= "/favicon.ico" uri)
        {:status 404}
        (let [res-data (-> (response/stored-data data-store req))]
          ;; TODO: Check if res-data is present in cache
          ;; TODO: pull current endpoint from config and call with same req uri
          (response res-data))))))

(defmethod ig/init-key ::service
  [_ {:keys [handler host port wrapper]}]
  (println "Running server on host:" host " port:" port)
  (let [app (-> handler
                wrap-json-response
                wrapper)]
    (jetty/run-jetty app {:host host :port port})))

(defmethod ig/halt-key! ::service
  [_ service]
  (.stop service))

(defn run-server [profile]
  (let [config (config/ig-config profile)]
    (config/init-config config)))

(defn -main [& args]
  (run-server :prod))
