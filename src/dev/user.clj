(ns user
  (:require [dantheobserver.parrot-api.config :refer [ig-config init-config]]
            [dantheobserver.parrot-api.app :refer [run-server]]
            [integrant.core :as ig]
            [puget.printer :refer [cprint]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]))

(defonce server (run-server :dev))

(defn stop [] (ig/halt! server))
(defn start [] (.start server))

(add-tap (bound-fn*
           (fn [x]
             (cond (= x :<) (println "**** begin ****")
                   (= x :>) (println "****  end  ****")
                   :else (cprint x)))))
