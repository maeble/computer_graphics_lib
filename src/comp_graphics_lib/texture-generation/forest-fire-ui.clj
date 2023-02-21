(ns comp-graphics-lib.texture-generation.forest-fire-ui
  (:require [cljfx.api :as fx]
            [comp-graphics-lib.texture-generation.map-utils :as mu]))

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
  (loop [mat matrix
         row 0
         col 0
         result []]
    (let [cell (mu/get-cell-value mat row col)
          new-result (if (= cell barren-key)
                       (conj result (get-rectangle row col barren-color))
                       (if (= cell forest-key)
                         (conj result (get-rectangle row col forest-color))
                         (if (= cell fire-key)
                           (conj result (get-rectangle row col fire-color)))))]
      (if (mu/has-next-index? mat row col)
        (recur mat ((mu/get-next-index mat row col) :row) ((mu/get-next-index mat row col) :col) new-result)
        new-result)
        )))


(defn root [{:keys [title
                    matrix forest-key forest-color
                    fire-key fire-color
                    barren-key barren-color
                    on-click-next-iteration-button
                    ]}]
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

;; Create renderer with middleware that maps incoming data - description -
;; to component description that can be used to render JavaFX state.
;; Here description is just passed as an argument to function component.

(defn renderer []
  (fx/create-renderer
   :middleware (fx/wrap-map-desc assoc :fx/type root)))

(defn show [*state renderer]
  (fx/mount-renderer *state renderer))
