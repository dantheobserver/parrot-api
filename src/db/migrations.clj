(ns migrations
  (:require [migratus.core :as migratus]))

(def config {:store :database
             :migration-dir "migrations/"
             ;; :init-script "init.sql"
             ;;script should be located in the :migration-dir path
             ;;defaults to true, some databases do not support
             ;;schema initialization in a transaction

             :init-in-transaction? false
             :migration-table-name "parrot_migrations"
             :db {:dbtype "postgres"
                  :dbname "parrot"
                  :user "postgres"
                  :password "docker"}
             })

(defn add-migration [name]
  (migratus/create config name))

(defn migrate []
  (migratus/init config)
  (migratus/migrate config))

;; Migration functions
