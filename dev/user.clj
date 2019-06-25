(ns user
  (:require [dantheobserver.parrot-api.app :as app]))

(defonce server (app/run-dev-server))

(defn stop [] (.stop server))
(defn start [] (.start server))
