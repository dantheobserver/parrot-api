(ns dantheobserver.parrot-api.request
  (:require [com.rpl.specter :as specter]
            [clojure.string :as string]))

(defn entry-path [uri]
  (let [[_ api-key path-key] (re-matches #"^/(.*?)/(.*)" uri)]
    [(keyword api-key) path-key]))

;; Request api follows - https://github.com/ring-clojure/ring/blob/master/SPEC

(defn request-data
  [db {:keys [uri query-string] :as req}]
  (let [path (entry-path uri)]
    (println path)
    (get-in @db path)))
