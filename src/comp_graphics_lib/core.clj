(ns comp-graphics-lib.core
  (:gen-class)
  (:require [comp-graphics-lib.texture-generation.forest-fire :as ff]))

(def fire-probability 0.05)
(def forest-probability 0.1)
(def number-of-rounds 3)
(def big-test-mat
  [[2 2 2 2 2 2 2 2 2 2]
   [2 2 1 2 2 2 2 2 2 2]
   [2 2 2 2 2 2 2 0 2 2]
   [2 0 0 2 2 2 2 2 2 2]
   [2 0 0 2 2 2 2 2 2 2]
   [2 2 2 2 1 2 2 2 2 2]
   [2 2 2 2 2 2 2 2 2 2]
   [2 0 0 2 2 2 2 2 2 2]
   [2 2 0 2 2 2 1 2 2 2]
   [2 2 2 2 2 2 2 2 2 2]])

;;(ff/run-forest-fire-main big-test-mat forest-probability fire-probability number-of-rounds)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
