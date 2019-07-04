(ns dantheobserver.parrot-api.response
  (:require [com.rpl.specter :as specter]
            [clojure.string :as string]))

(defn entry-path
  "Fetch key for db based on path"
  [uri]
  (let [[_ api-key path-key] (re-matches #"^/(.*?)/(.*)" uri)]
    [(keyword api-key) path-key]))

;; Request api follows - https://github.com/ring-clojure/ring/blob/master/SPEC

(defn stored-data
  "Fetch response data from `db` given a `req`"
  [db {:keys [uri query-string] :as req}]
  (let [path (entry-path uri)]
    (get-in @db path)))
