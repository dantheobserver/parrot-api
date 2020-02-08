(ns dantheobserver.parrot-api.response
  "functions responsible for converting request data to
  a valid response object which will be passed back as a response"
  (:require [clojure.edn :as edn]
            [clojure.core.reducers :as r]
            [com.rpl.specter :as specter
             :refer [MAP-VALS ALL STAY select transform setval recursive-path if-path]]
            [clojure.string :as string]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]))

;;TODO - Add :static-template meta to stored value to shortcut 
;;TODO - Validate functons found in response to be of this nmespace

(declare query-ref)
(declare header-ref)
(declare loop-data)

(defn node-path
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
  "Returns a functon that returns its request argument
  by `path` and returns `default-val` if nil."
  [path default-val]
  (fn [request]
    (get-in request path default-val)))

(defn query-ref
  "Returns function that accepts a request and
  returns the query value of the response by`query-key` or `default-value`"
  ([[query-key default-val]] (query-ref query-key default-val))
  ([query-key default-val]
   (request-ref [:query query-key] default-val)))

(defn header-ref
  "Returns function that accepts a request and
  returns the header value of the response by`header-key` or `default-value`"
  ([[header-key default-val]] (header-ref header-key default-val))
  ([header-key default-val]
   (request-ref [:headers header-key] default-val)))

(defn loop-data
  "Returns a function that repeats the `data` given by a count
  specified by `path` in the `request`."
  ([[path data]] (loop-data path data))
  ([path data]
   (fn [request]
     (if-let [count (get-in request path)]
       (let [COUNT-VALUES (node-path #(= % :parrot/loop-count))]
         (into [] (for [i (range 1 (inc count))]
                    (setval [COUNT-VALUES] i data))))))))

;; TODO, keys should be normalized to trim last ? or & from end
(defn query-obj
  "Returns a proper map of the key if it has one. If there is no query it returns nil"
  [key]
  (let [normal-key (str/replace key #"(\?|&)$" "")]
    (when-let [q-start (str/index-of normal-key "?")]
      (let [key-query (-> (subs normal-key (inc q-start))
                          (str/split #"&"))]
        (transduce (comp (map #(str/split % #"="))
                         (map (fn [[k v]]
                                [(keyword k) v])))
                   conj
                   {}
                   key-query)))))

(defn template-key
  "Selects the proper key in the store given a `request` and `store`"
  [{:keys [uri query]} store]
  (if-let [matching-keys (filter #(str/starts-with? % uri)(keys store))]
    (if (> 1 (count matching-keys))
      nil
      (first matching-keys))
    ))
(template-key {:uri "/test1"} {"/test1" nil})
(template-key {:uri "/test1"} {"/test1" nil
                               "/test1" nil})
#_(str/replace "testing?" #"\?|&$" "")
#_(str/replace "testing?" #"\?|&$" "")

#_(let [key "/test/matchapi/static?testing=1"]
    (if-let [q-start (str/index-of key "?")]
      (let [key-query (-> (subs key (inc q-start))
                          (str/split #"&"))]
        (transduce (comp (map #(str/split % #"="))
                         (map (fn [[k v]]
                                [(keyword k) v])))
                   conj
                   {}
                   key-query))))

(str/split "test=1" #"=")

;; TODO: Account for null api-key, in the case.
(defn stored-response-data
  "Retrieves response data from stored templates"
  [{:keys [uri] :as request} store]
  (when-let [template (get store (template-key request store))]
    (resolved-template template request)))
;; Request api follows - https://github.com/ring-clojure/ring/blob/master/SPEC

(def ^:private default-readers {'parrot/query-ref query-ref
                                'parrot/header-ref header-ref
                                'parrot/loop-data loop-data})

(comment
  (specter/setval [(node-path #(= % 1))]
                  22 {:a {:b 1}})

  (specter/setval [(node-path #(= % :parrot/test))]
                  22 {:a {:b :parrot/test}})
  (let [FIND-FNS (node-path fn?)]
    (select [FIND-FNS] {:a 1
                        :b identity
                        :c [+]
                        :d [{:d -}]}))

  (let [template (edn/read-string
                   {:readers default-readers}
                   "{:test #parrot/query-ref [:test \"default\"]}")]
    (resolved-template template {:query {:test 1}})))
