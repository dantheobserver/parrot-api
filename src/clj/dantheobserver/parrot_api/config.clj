(ns dantheobserver.parrot-api.config
  (:require [aero.core :as aero]
            [clojure.string :as string]
            [dantheobserver.parrot-api.store :refer [sql]]
            [integrant.core :as ig]))

(defmethod aero/reader 'parrot/sql
  [_ _ value]
  (clojure.string/join " " value))

(defn ig-config [profile]
  (binding [*data-readers* {'ig/ref ig/ref}]
    (-> (clojure.java.io/resource "config.edn")
        (aero/read-config {:profile profile}))))

(defn init-config [config] (ig/init
                             (dissoc config :app-config)))
