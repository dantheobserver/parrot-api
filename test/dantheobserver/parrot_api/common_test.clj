(ns dantheobserver.parrot-api.common-test
  (:require [dantheobserver.parrot-api.common :refer :all]
            [clojure.test :as t]))

(t/deftest common
  (t/testing "fill"
    (t/is (= [:some-key] (fill [:default] [:some-key])))
    (t/is (= [:default] (fill [:default] [])))
    (t/is (= [:default-a :default-b] (fill [:default-a :default-b] [])))
    (t/is (= [:default-a :b] (fill [:default-a :default-b] [nil :b])))
    (t/is (= [:a :default-b] (fill [:default-a :default-b] [:a])))
    (t/is (= [:a :b] (fill [:default-a :default-b] [:a :b])))))
