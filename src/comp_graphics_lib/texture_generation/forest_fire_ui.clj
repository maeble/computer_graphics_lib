(ns comp_graphics_lib.texture_generation.forest_fire_ui
  (:require [cljfx.api :as fx]
            [comp_graphics_lib.texture_generation.map_utils :as mu]
            [clojure.core.match :as match]))

;; Define application state
(defn get-state [window-title matrix 
                 forest-key forest-color
                 fire-key fire-color
                 barren-key barren-color
                 on-click-next-iteration-button
                 ]
  (atom {:title window-title
         :matrix matrix
         :forest-key forest-key
         :forest-color forest-color
         :fire-key fire-key 
         :fire-color fire-color
         :barren-key barren-key 
         :barren-color barren-color
         :on-click-next-iteration-button on-click-next-iteration-button})) 

(defn get-rectangle [row col color] 
  {:fx/type :rectangle
   :width 7
   :height 7
   :grid-pane/row (inc row)
   :grid-pane/column (inc col)
   :fill color}
  )

(defn matrix-2-map [matrix
                    forest-key forest-color
                    fire-key fire-color
                    barren-key barren-color]
  (loop [mat matrix, row 0, col 0, result []]
    (let [cell-value (mu/get-cell-value mat row col)
          new-result (match/match [cell-value] ;; simple pattern matching
                       [barren-key] (conj result (get-rectangle row col barren-color))
                       [forest-key] (conj result (get-rectangle row col forest-color))
                       [fire-key] (conj result (get-rectangle row col fire-color)))]
      (if (mu/has-next-index? mat row col)
        (let [[next-row next-col] (mu/get-next-cell mat row col)] ;; seq destructuring
          (recur mat next-row next-col new-result))
        new-result)
        )))


(defn root [{:keys [title
                    matrix forest-key forest-color
                    fire-key fire-color
                    barren-key barren-color
                    on-click-next-iteration-button
                    ]}] ;; uses associative destructuring of the atom dictionary of *state
  (println "root: create window, map, etc")
  {:fx/type :stage
   :showing true
   :title title
   :scene {:fx/type :scene
           :root {:fx/type :v-box
                  :alignment :center
                  :spacing 10.0
                  :children [{:fx/type :label
                              :style {:-fx-text-fill :black, :-fx-font-weight :bold}
                              :text ""}
                             {:fx/type :label
                              :style {:-fx-text-fill :black, :-fx-font-weight :bold}
                              :text "Interactive Texture Generation"}
                             {:fx/type :label
                              :text "This is an interactive demonstration of the forest fire texture generation algorithm."}
                             {:fx/type :button
                              :text ">> next iteration"
                              :on-action on-click-next-iteration-button}
                             {:fx/type :grid-pane 
                              :children (matrix-2-map  matrix forest-key forest-color
                                                       fire-key fire-color
                                                       barren-key barren-color)
                              }
                             ]}}})

(defn renderer []
  (fx/create-renderer
   :middleware (fx/wrap-map-desc assoc :fx/type root)))

(defn show [*state renderer]
  (fx/mount-renderer *state renderer))
