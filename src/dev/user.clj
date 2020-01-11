(ns user
  (:require [dantheobserver.parrot-api.config :refer [ig-config init-config]]
            [dantheobserver.parrot-api.app :refer [run-server]]
            [integrant.core :as ig]
            [puget.printer :refer [cprint]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]))

(println "\n\n\n\n\n******************DEV SERVER**********************\n\n\n\n")
(defonce server (run-server :dev))

(defn stop [] (ig/halt! server))

(add-tap (bound-fn*
           (fn [x]
             (cond (= x :<) (println "**** begin ****")
                   (= x :>) (println "****  end  ****")
                   :else (cprint x)))))
