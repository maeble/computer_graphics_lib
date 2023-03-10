(ns comp_graphics_lib.core
  (:gen-class)
  (:require [comp_graphics_lib.texture_generation.forest_fire :as ff]
            [comp_graphics_lib.texture_generation.forest_fire_ui :as ui])
  (:import [javafx.application Platform])
  )

;; Configurations
;; --------------------------------------------------------------------------------------------
(def map-size 80)

(def fire-probability 0.0005)
(def forest-probability 0.02)

(def fire-weight 4)
(def forest-weight 1000)
(def barren-weight 25)

;; Code
;; --------------------------------------------------------------------------------------------

(defn run-forest-fire-loop [fire-probability forest-probability renderer map]
  (let [*state (ui/get-state "Interactive Forest Fire Demonstration"
                             map
                             ff/tree :darkolivegreen
                             ff/fire :darkorange
                             ff/barren :black
                             (fn [_] (run-forest-fire-loop fire-probability forest-probability renderer (ff/transform-mat map forest-probability fire-probability)))
                             )]
    (ui/show *state renderer)))


(defn run-forest-fire-demo-ui [map-size
                               fire-probability forest-probability
                               fire-weight forest-weight barren-weight]
  (run-forest-fire-loop fire-probability forest-probability
                        (ui/renderer) (ff/get-new-random-map map-size map-size forest-weight fire-weight barren-weight)) 
  ) ;; Note: recur was not possible here, as surrounding macro contained loop and I could not reach my own loop

;; RUN (REPL)
;;  (run-forest-fire-demo-ui map-size
;;                           fire-probability forest-probability
;;                           fire-weight forest-weight barren-weight)


(defn -main
  "Run the forest-fire algorithm"
  [& args]
  (Platform/setImplicitExit true)
  (println "Run Forest Fire")
  (run-forest-fire-demo-ui map-size
                           fire-probability forest-probability
                           fire-weight forest-weight barren-weight)
  )
