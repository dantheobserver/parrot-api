(ns dantheobserver.parrot-api.config
  (:require [dantheobserver.parrot-api.store :refer [sql]]
            [integrant.core :as ig]))

(defn ig-config
  [{:keys [host port wrapper]}]
  {:database.sql/datasource
   {:dbtype "postgres"
    :dbname "parrot"
    :user "postgres"
    :password (get (System/getenv) "DATABASE_PW")}

   :dantheobserver.parrot-api.store/data-store
   {:datasource (ig/ref :database.sql/datasource)
    :query (sql "select api_key.name, response.endpoint, response.query,"
                "response.response, response.match_query"
                "from response join api_key on api_key.id = response.api_id")}

   :dantheobserver.parrot-api.app/handler
   {:data-store (ig/ref :dantheobserver.parrot-api.store/data-store)}

   :dantheobserver.parrot-api.app/service
   {:handler (ig/ref :dantheobserver.parrot-api.app/handler)
    :host (or host "0.0.0.0")
    :port (or port 3030)
    :wrapper (or wrapper identity)}})
