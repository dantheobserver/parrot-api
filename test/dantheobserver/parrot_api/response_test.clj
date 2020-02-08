(ns dantheobserver.parrot-api.response-test
  (:require [clojure.test :refer :all]
            [dantheobserver.parrot-api.response :refer :all]
            [com.rpl.specter :as s]))

(defn is=
  ([expected actual] (is (=  expected actual))) 
  ([expected actual msg] (is (= expected actual) msg)))

(deftest node-path-fn
  (testing "Returns a path that can find a node by key"
    (let [test-data {:top {:key-value :test-key
                           :nested {:fn-value (fn [] "test-result")}}}]
      (is= nil (s/select-one [(node-path #(= % :nonexistent-key))] test-data))
      (is= :test-key (s/select-one [(node-path #(= % :test-key))] test-data))
      (is= "test-result" (let [value-fn (s/select-one [(node-path fn?)] test-data)]
                           (value-fn))))))

(deftest resolving-templates
  (testing "resolves a template based on a request"
    (is= {:shallow "static"} (resolved-template {:shallow "static"} nil))
    (is= {:shallow "evaluated"} (resolved-template {:shallow (fn [_]
                                                               "evaluated")} nil))
    (is= {:echo  {:echo "echo"}} (resolved-template {:echo identity}
                                                    {:echo "echo"})))

  (testing "query ref returns function that evaluates on request"
    (let [test-request {:uri "test/"
                        :query {:testing "query-value"}
                        :headers {:testing "header-value"}}]
      (is= "query-value" ((query-ref :testing nil) test-request))
      (is= "header-value" ((header-ref :testing nil) test-request))
      (is= nil ((query-ref :nothing nil) test-request))
      (is= "default-value" ((query-ref :nothing "default-value") test-request))))

  (testing "multi response matching based on request parameters"
    (let [test-repo {"/test1"}]))
  )

#_(deftest generating-query-obj
    (is= nil (query-obj "test/"))
    (is= {:test-value "1"} (query-obj "test?test-value=1"))
    (is= {:val-a "1" :val-b "hello"} (query-obj "test?val-b=hello&val-a=1&")))

#_(deftest get-template-store-key
    (testing "it should return a key in the store if it matches the request"
      (let [store {"/test1" nil}]
        (is= nil (template-key {:uri "/test0"} {"/test1" nil}))
        (is= "/test1" (template-key {:uri "/test1"} {"/test1" nil}))))
    (testing "it should be able to match based on query parameters if specified by the store"
      (let [store {"/test1?test-value=1" nil
                   "/test1?test-value=2" nil}]
        (is= nil (template-key {:uri "/test1" :query {:test-value 2}}
                               store)))))


(deftest stored-response-data-fn
  (let [template-store {"/test/matchapi/static"
                        {:data {:some-data "test"}}

                        "/test/matchapi/query"
                        {:data {:some-data
                                (query-ref [:x-query-value "default"])}}

                        "/test/matchapi/query?matches=exactly"
                        {:data "exact-query-match"}

                        "/test/matchapi/header"
                        {:data {:some-data
                                (header-ref [:x-header-value "missing"])}}

                        "/test/matchapi/multi"
                        {:data
                         {:some-data
                          (loop-data
                            ;;TODO also allow function
                            [:query :limit]
                            {:id :parrot/loop-count})}}

                        "/test/matchapi/custom"
                        {:data
                         {:some-data
                          (fn [_]
                            (into [] (for [i (range 5)]
                                       {:id i :name (str "dummy-data-" i)})))}}
                        }]

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

    (testing "uri with matching template with query data"
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
           (stored-response-data {:uri "/test/matchapi/multi" :query {:limit 5}} template-store)))
    (testing "Expading template with some arbitrary function"
      (is= {:data {:some-data [{:id 0 :name "dummy-data-0"}
                               {:id 1 :name "dummy-data-1"}
                               {:id 2 :name "dummy-data-2"}
                               {:id 3 :name "dummy-data-3"}
                               {:id 4 :name "dummy-data-4"}]}}
           (stored-response-data {:uri "/test/matchapi/custom"} template-store)))))
