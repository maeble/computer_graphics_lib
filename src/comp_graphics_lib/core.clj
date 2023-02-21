(ns comp-graphics-lib.core
  (:gen-class)
  (:require [comp-graphics-lib.texture-generation.forest-fire :as ff]
            [comp-graphics-lib.texture-generation.forest-fire-ui :as ui]))

;; Configurations

(def map-size 40)

(def fire-probability 0.05)
(def forest-probability 0.1)
(def number-of-rounds 3)

(def fire-weight 1)
(def forest-weight 100)
(def barren-weight 5)

;; plain forest fire (no ui)
;;(ff/run-forest-fire-main big-test-mat forest-probability fire-probability number-of-rounds)

;; ui-based forest fire
(defn run-forest-fire-loop [fire-probability forest-probability renderer map]
  (let [*state (ui/get-state "Interactive Forest Fire Demonstration"
                             map
                             ff/tree :darkolivegreen
                             ff/fire :darkorange
                             ff/barren :black
                             (fn [_] (run-forest-fire-loop fire-probability forest-probability renderer (ff/run-forest-fire-main map forest-probability fire-probability 1))))]
    (ui/show *state renderer)))

(defn run-forest-fire-demo-ui [map-size
                               fire-probability forest-probability
                               fire-weight forest-weight barren-weight]
  (run-forest-fire-loop fire-probability forest-probability
                        (ui/renderer) (ff/get-new-random-map map-size map-size forest-weight fire-weight barren-weight)) 
  ) ;; Note: recur was not possible here, as surrounding macro contained loop and I could not reach my own loop


 (run-forest-fire-demo-ui map-size fire-probability forest-probability 
                          fire-weight forest-weight barren-weight)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
