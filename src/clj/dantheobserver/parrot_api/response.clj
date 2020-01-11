(ns dantheobserver.parrot-api.response
  "functions responsible for converting request data to
  a valid response object which will be passed back as a response"
  (:require [clojure.edn :as edn]
            [com.rpl.specter :as specter
             :refer [MAP-VALS ALL STAY select transform setval recursive-path if-path]]
            [clojure.string :as string]))

;;TODO - Validate functons found in response to be of this nmespace
;;TODO: Add :static-template meta to stored value to shortcut 

;; Example
;; request
#_{:uri "some/endpoint/"
   :headers nil}
;; store
#_^:isStatic{:uri some/endpoint
             :data {;;constant string
                    :some-data "test" 
                    
                    ;; reference-function to the query-string optional default value.
                    :some-query-data (query-ref :some-query-value "default-value")

                    ;; reference-function to the response headers optional default value.
                    }}

(def ^:private default-readers {'parrot/query-ref query-ref})

(defn node-finder
  "Generates a recursive-path finder that visits
  matching `pred-fn`"
  [pred-fn]
  (recursive-path
    [] p
    (specter/cond-path
      pred-fn STAY
      map? [MAP-VALS p]
      vector? [ALL p])))

;; Template resolution

;; TODO: validate that fn template validation
(defn resolved-template
  "Convert a template into the resolved data"
  [template request]
  (let [RESOLVE-FNS (recursive-path [] p
                                    (specter/cond-path
                                      fn? STAY
                                      map? [MAP-VALS p]
                                      vector? [ALL p]))]
    (transform [RESOLVE-FNS]
               (fn [res-fn] (res-fn request))
               template)))

(defn request-ref
  [path default-val]
  (fn [request]
    (get-in request path default-val)))

(defn query-ref
  ([[query-key default-val]]
   (request-ref [:query query-key] default-val)))

(defn header-ref
  ([[header-key default-val]]
   (request-ref [:headers header-key] default-val)))

(defn loop-data
  [path data]
  (fn [response]
    (if-let [count (get-in response path)]
      (let [LOOP-KEY (node-finder #(= % :parrot/loop-count))]
        (into [] (for [i (range 1 (inc count))]
                   (setval [LOOP-KEY] i data)))))))

#_(specter/setval [(node-finder #(= % 1))]
                  22 {:a {:b 1}})

#_(specter/setval [(node-finder #(= % :parrot/test))]
                  22 {:a {:b :parrot/test}})
;; TODO: Account for null api-key, in the case.
(defn stored-response-data
  "Retrieves response data from stored templates"
  [{:keys [uri] :as request} store]
  (when-let [template (get store uri)]
    (resolved-template template request)))
;; Request api follows - https://github.com/ring-clojure/ring/blob/master/SPEC

(comment
  (let [FIND-FNS (node-finder fn?)]
    (select [FIND-FNS] {:a 1
                        :b identity
                        :c [+]
                        :d [{:d -}]}))

  (let [template (edn/read-string
                   {:readers default-readers}
                   "{:test #parrot/query-ref [:test \"default\"]}")]
    (resolved-template template {:query {:test 1}})))
