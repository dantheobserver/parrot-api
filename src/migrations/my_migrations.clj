(ns my-migrations
  (:require [migratus.core :as migratus]))

(def config {:store :database
             :migration-dir "migrations/"
             :init-script "init.sql" ;script should be located in the :migration-dir path
                                        ;defaults to true, some databases do not support
                                        ;schema initialization in a transaction
             :init-in-transaction? false
             :migration-table-name "parrot"
             :db {:classname "org.postgresql.Driver"
                  :subprotocol "postgresql"
                  :subname "parrot.db"
                  :username "postgres"
                  }})

(def config)
