(ns dantheobserver.parrot-api.response-test
  (:require [clojure.test :refer :all]
            [dantheobserver.parrot-api.response :refer :all]))

(defn is= [expected actual] (is (= expected actual)))

(deftest request-processing
  (let [template-store {"/test/matchapi/static" {:data {:some-data "test"}}
                        "/test/matchapi/query" {:data {:some-data
                                                       (query-ref [:x-query-value "default"])}}
                        "/test/matchapi/header" {:data {:some-data
                                                        (header-ref [:x-header-value "missing"])}}
                        "/test/matchapi/multi" {:data
                                                {:some-data
                                                 (loop-data
                                                   ;;TODO also allow function
                                                   [:query :limit]
                                                   {:id :parrot/loop-count})}}}]

    (testing "uri no query or header match with static template"
      (is= nil (stored-response-data {:uri "/test/mismatch"} template-store))
      (is= {:data {:some-data "test"}}
           (stored-response-data {:uri "/test/matchapi/static"} template-store))
      (is= {:data {:some-data "test"}}
           (stored-response-data {:uri "/test/matchapi/static" :query {:insignificant true}}
                                 template-store))
      (is= {:data {:some-data "test"}}
           (stored-response-data {:uri "/test/matchapi/static" :header {:x-useless true}}
                                 template-store)))

    (testing "uri with matching template with query"
      (is= {:data {:some-data 12}}
           (stored-response-data {:uri "/test/matchapi/query" :query {:x-query-value 12}}
                                 template-store))
      (is= {:data {:some-data "default"}}
           (stored-response-data {:uri "/test/matchapi/query"} template-store)))
    (testing "uri with matching template with header"
      (is= {:data {:some-data 12}}
           (stored-response-data {:uri "/test/matchapi/header" :headers {:x-header-value 12}}
                                 template-store))
      (is= {:data {:some-data "missing"}}
           (stored-response-data {:uri "/test/matchapi/header"} template-store)))
    (testing "Expanding template to multiple rows based on parameter"
      (is= {:data
            {:some-data
             [{:id 1}
              {:id 2}
              {:id 3}
              {:id 4}
              {:id 5}]}}
           (stored-response-data {:uri "/test/matchapi/multi" :query {:limit 5}}
                                 template-store)))))
