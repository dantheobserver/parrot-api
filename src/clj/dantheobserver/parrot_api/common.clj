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

