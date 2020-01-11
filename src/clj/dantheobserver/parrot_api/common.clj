(ns dantheobserver.parrot-api.common)

(defn fill
  "fill values from `a-vec` into `b-vec` if value at position is nil."
  [a-vec b-vec]
  (loop [[a & as] a-vec
         [b & bs] b-vec
         val []]
    (cond
      (and (nil? a)
           (nil? b)) val
      (nil? a) (recur as bs (conj val b))
      (nil? b) (recur as bs (conj val a))
      :else (recur as bs (conj val b)))))


#_(defn local-state [initial-value]
    (let [val (atom initial-value)
          get-fn (fn [] @val)
          set-fn (fn [v] (reset! val v))]
      [get-fn set-fn]))

#_(let [[current-name set-name] (local-state "")]
    (println "no change: " (current-name))
    (set-name "daniel")
    (println "set name: " (current-name))
    (set-name "Some-other-name")
    (println "set name 2: " (current-name))
    )

