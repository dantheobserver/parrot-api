(ns dantheobserver.parrot-api.app
  (:require [dantheobserver.parrot-api.response :as response]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [dantheobserver.parrot-api.common :refer [fill]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [clojure.string :refer [join]]
            [clojure.data.json :as json]))

;; Seed data
(def ds (jdbc/get-datasource {:dbtype "postgres"
                              :dbname "parrot"
                              :user "postgres"
                              :password (get (System/getenv) "DATABASE_PW")}))

(defn key-selector [row]
  (select-keys row [:name :endpoint :query :response :match_query]))

(defn sql [& stmts] (join " " stmts))

(defn pgobj->map [pgObj]
  (if pgObj 
    (json/read-str (.getValue pgObj) :key-fn keyword)
    pgObj))

(defn json-converter [row]
  (-> row
      (update :response pgobj->map)
      (update :query pgobj->map)))

(def query [(sql "select api_key.name, response.endpoint, response.query,"
                 "response.response, response.match_query"
                 "from response join api_key on api_key.id = response.api_id")])

;; TODO: Spec out db object
(def data-store
  (->> (jdbc/plan ds query)
       (into #{} (comp (map key-selector)
                       (map json-converter)))))

(def config {:api1 "https://parrot-test.free.beeceptor.com"})

(defn handler [{:keys [uri] :as req}]
  (if (= "/favicon.ico" uri)
    {:status 404}
    (let [res-data (-> (response/stored-data data-store req))]
      ;; TODO: Check if res-data is present in cache
      ;; TODO: pull current endpoint from config and call with same req uri
      ;; TODO: store result in cache db
      (response res-data))))

(def app (-> handler
             wrap-json-response))

(defn -main [& args]
  (let [[hostname port] (fill ["127.0.0.1" 3030] args)]
    (println "Running server on host:" hostname " port:" port)
    (jetty/run-jetty app {:host hostname :port port})))
