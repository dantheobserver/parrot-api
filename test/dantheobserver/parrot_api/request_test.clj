(ns dantheobserver.parrot-api.request-test
  (:require [clojure.test :refer :all]
            [dantheobserver.parrot-api.request :refer :all]))

(deftest request-processing
  (is (= (entry-path "api1/test-endpoint") [:api1 "test-endpoint"]))
  (let [test-db {:api1
                 {"test-endpoint" {:data {:name "test"}}}
                 :api-query
                 {"text-endpoint" {:data {:name "test"}
                                   :query-pattern {:a :any?
                                                   :b :number?}}}}]
    (testing "Getting data for requests"
      (are [endpoint data] (= (request-data test-db endpoint) data)
        {:uri "api1/test-endpoint2"} nil
        {:uri "api1/test-endpoint"} {:data {:name "test"}}

        ;;query matching
        {:uri "api1/test-endpoint"
         :query-string "ignore=this"} {:data {:name "test"}}))))
