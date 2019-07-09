(ns dantheobserver.parrot-api.store
  (:require [clojure.data.json :as json]
            [clojure.string :refer [join]]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defn sql [& stmts] (join " " stmts))

(defn key-selector [row]
  (select-keys row [:name :endpoint :query :response :match_query]))

(defn pgobj->map [pgObj]
  (if pgObj
    (json/read-str (.getValue pgObj) :key-fn keyword)
    pgObj))

(defn json-converter [row]
  (-> row
      (update :response pgobj->map)
      (update :query pgobj->map)))

(defmethod ig/init-key ::data-store
  [_ {:keys [datasource query]}]
  (let [result-set (jdbc/plan datasource [query])]
    (into #{} (comp (map key-selector)
                    (map json-converter))
          result-set)))
