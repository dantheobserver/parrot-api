(ns dantheobserver.parrot-api.common-test
  (:require [dantheobserver.parrot-api.common :refer :all]
            [clojure.test :as t]))

(t/deftest common
  (t/testing "with-args"
    (t/is (= :some-key (with-args [:some-key]
                         [value :default]
                         value)))
    (t/is (= :default (with-args []
                        [value :default]
                        value)))
    (t/is (= [:default-a :default-b]
             (with-args []
               [val-a :default-a
                val-b :default-b]
               [val-a val-b])))
    (t/is (= [:default-a :b] (with-args [nil :b]
                               [val-a :default-a
                                val-b :default-b]
                               [val-a val-b])))))
