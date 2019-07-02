(ns dantheobserver.parrot-api.response.spec
  (:require [clojure.spec.alpha :as s]
            [dantheobserver.parrot-api.response :as response]))

;; Response
(s/def ::uri string?)
(s/def ::query-string string?)
(s/def ::request (s/keys :req-un [::url]
                         :opt-un [::query-string ::headers]))

(s/fdef response/stored-data
  :args (s/cat :request ::request)
  :ret (s/or :response map?
             :nothing nil?))
