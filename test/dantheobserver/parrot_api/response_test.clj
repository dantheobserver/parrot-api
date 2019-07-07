(ns dantheobserver.parrot-api.response-test
  (:require [clojure.test :refer :all]
            [dantheobserver.parrot-api.response :refer :all]))

(deftest request-processing
  (is (= (entry-path "api1/test-endpoint") [:api1 "test-endpoint"])))
