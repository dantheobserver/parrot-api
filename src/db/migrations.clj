(ns migrations
  (:require [migratus.core :as migratus]
            [dantheobserver.parrot-api.common :refer [fill]]))

(def config {:store :database
             :migration-dir "migrations/"
             ;;TODO: Create script to setup database and user.
             ;; :init-script "init.sql"
             ;;script should be located in the :migration-dir path
             ;;defaults to true, some databases do not support
             ;;schema initialization in a transaction

             :init-in-transaction? false
             :migration-table-name "parrot_migrations"
             :db {:dbtype "postgres"
                  :dbname "parrot"
                  :user "postgres"
                  :password (get (System/getenv) "DATABASE_PW")}})

(defn add-migration [name]
  (migratus/create config name))

(defn migrate []
  (migratus/init config)
  (migratus/migrate config))

(defn rollback []
  (migratus/rollback config))

(defn -main [& args]
  (let [[command arg] args]
    (cond
      (nil? command) (migrate)
      (= "add" command) (add-migration arg)
      (= "rollback" command) (rollback))))
