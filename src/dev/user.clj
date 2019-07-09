(ns user
  (:require [dantheobserver.parrot-api.config :refer [ig-config]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [puget.printer :refer [cprint]]
            [integrant.core :as ig]))

(defn run-dev-server []
  (let [dev-config (ig-config {:host "127.0.0.1"
                               :wrapper wrap-reload})]
    (println "---Starting dev server---")
    (ig/init config)))

(defonce server (run-dev-server))

(defn stop [] (ig/halt! server))
;; (defn start [] (.start server))

(add-tap (bound-fn*
           (fn [x]
             (cond (= x :<) (println "**** begin ****")
                   (= x :>) (println "****  end  ****")
                   :else (cprint x)))))
