(ns dantheobserver.parrot-api.spec.response
  (:require [malli.core :as m]))

(def Template
  [:and
   {:title "Template"
    :description "Response template to be resolved as response data"
    :json-schema/example {:data {:some-data "test"}}}
   [:map
    [:data
     [:map]]]])

#_(m/validate Template {:data {:a 1}})
#_(re-matches #"(\w+/?)+" "fave/test")

(def Request
  [:and
   {:title "Request"
    :description "Data representing a request"}
   [:map
    [:uri string?]
    [:query {:optional true} :map]
    [:header {:optional true} :map]]])

#_(m/validate Request {:uri "test"
                       :query {:a 1}
                       :header {:a 1}})
