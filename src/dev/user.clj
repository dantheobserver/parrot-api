(ns user
  (:require [dantheobserver.parrot-api.app :refer [app]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [puget.printer :refer [cprint]]))

(defn run-dev-server []
  (println "---Starting dev server---")
  (jetty/run-jetty (wrap-reload app) {:port 3030 :join? false}))

(defonce server (run-dev-server))

(defn stop [] (.stop server))
(defn start [] (.start server))

(add-tap (bound-fn*
           (fn [x]
             (cond (= x :<) (println "**** begin ****")
                   (= x :>) (println "****  end  ****")
                   :else (cprint x)))))
