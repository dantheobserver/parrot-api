(ns dantheobserver.parrot-api.common)

(defmacro with-args
  "Bind a sequence of values to defaults listed in `arg-bindings` and evaluate body."
  {:style/indent [1]} 
  [argv arg-bindings & body]
  (let [sym-default-pairs (partition 2 arg-bindings) 
        argv (let [argv-count (count argv)
                   def-count (count sym-default-pairs)
                   diff (- def-count argv-count)]
               (if (< argv-count def-count) 
                 (concat argv (repeat diff nil))
                 argv))
        syms (mapv first sym-default-pairs)
        default-vals (mapv #(or %1 %2)
                           argv
                           (mapv last sym-default-pairs))]
    `(let [~syms ~default-vals]
       ~@body)))

