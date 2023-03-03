(ns comp_graphics_lib.color_generation.text_to_color_ui
  (:require [cljfx.api :as fx]
            [comp_graphics_lib.texture_generation.map_utils :as mu]
            [comp_graphics_lib.color_generation.text_color_transformation :as text2color]
            ))

(defn rgb-2-hexadezimal [rgb-vec]
  (let [[r g b] rgb-vec]
    (format "#%02x%02x%02x", r, g, b)))

;; Define application state
(defn get-state [window-title matrix on-click-generate-button]
  (atom {:title window-title
         :matrix matrix
         :on-click-generate-button on-click-generate-button}))

(defn get-rectangle [row col color]
  {:fx/type :rectangle
   :width 15
   :height 15
   :grid-pane/row (inc row)
   :grid-pane/column (inc col)
   :fill (rgb-2-hexadezimal color)})

(defn matrix-2-map [matrix]
  (loop [mat matrix, row 0, col 0, result []]
    (let [cell-value (mu/get-cell-value mat row col)
          new-result (conj result (get-rectangle row col cell-value))]
      (if (mu/has-next-index? mat row col)
        (let [[next-row next-col] (mu/get-next-cell mat row col)] ;; seq destructuring
          (recur mat next-row next-col new-result))
        new-result))))


(defn root [{:keys [title
                    matrix
                    on-click-generate-button]}] ;; uses associative destructuring of the atom dictionary of *state
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
                              :text "    Text to Texture Generation    "}
                            ;;  {:fx/type :label
                            ;;   :style {:-fx-text-fill :black, :-fx-font-weight :bold}
                            ;;   :text "    Interactive Text to Texture Generation    "}
                            ;;  {:fx/type :label
                            ;;   :text "Enter a text and generate a texture."}
                            ;;  {:fx/type :button
                            ;;   :text ">> Generate texture"
                            ;;   :on-action on-click-generate-button} 
                             {:fx/type :grid-pane
                              :alignment :center
                              :children (matrix-2-map  matrix)}
                             {:fx/type :label
                              :style {:-fx-text-fill :black, :-fx-font-weight :bold}
                              :text ""}]}}})

(defn renderer []
  (fx/create-renderer
   :middleware (fx/wrap-map-desc assoc :fx/type root)))

(defn show [*state renderer]
  (fx/mount-renderer *state renderer))


;; RUN (REPL)
;; Note: not integrated yet in core / lein run

;; (def texture-mat (text2color/strings-to-texture-map "num is used to coerce a primitive Java number type such as int, float, long, double, etc., into its boxed version such as Float, Long, Double, etc. If given an existing boxed Number type, as opposed to a primitive number type, it will just return it as is."))
;; (show (get-state "Text to color map generation" texture-mat (fn [_] (prn "click")))
;;       (renderer))
