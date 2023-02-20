;; cellular automation: forest fire model
;; https://www.mdpi.com/1424-8220/20/4/1135/html#sec2-sensors-20-01135

;; 0 empty - 1 fire - 2 forest
;; forest-probability (e.g., 0.01): 0 -> 2
;; fire-probability (e.g., 0.000005): 2 -> 1

;; transformations in each iteration:
;; 0 -> 2 | forest-probability
;; 2 -> 1 | fire-probability
;; fire goes out: 1 -> 0
;; if a neighbour of forest cell is burning, cell will burn too
;; -> 2|1 -> 1|0
;; -> 0|1 -> 0|0

(ns comp-graphics-lib.texture-generation.forest-fire
  (:require
    [comp-graphics-lib.texture-generation.map-utils :as map-utils])
  (:import )
  )


;; configurations ========================================

(def tree 2)
(def fire 1)
(def barren 0)

;; code ==================================================

(defn burn?
  "Returns true if the cell should start burning.
   Returns false otherwise."
  [cell probability]
  (if (= cell tree)
    (> probability (rand))
    false))

(defn grow?
  "Returns true if the cell should grow a tree.
   Returns false otherwise."
  [cell probability]
  (if (= cell barren)
    (> probability (rand))
    false))

(defn on-fire? [cell]
  (= cell fire))

;; # Clj can use java functions with .javaFuncName 
;; # uses recursive anonymous function; termination conition = invalid row index
(defn contains-fire?
  "Returns true if the map matrix has a burning cell.
   Returns false otherwise."
  [mat]
  ((fn _contains-fire? [mat row]
     (if (contains? mat row)
       (if (.contains (nth mat row) fire)
         true
         (_contains-fire? mat (inc row)))
       false))
   mat 0))

(defn neighbour-on-fire? 
  "Returns true if the neighbour cell of the given key is on fire."
  [mat row col neighb]
  (if (nil? ((map-utils/get-neighbours mat row col) neighb))
    false
    (= (((map-utils/get-neighbours mat row col) neighb) :val) fire)))

(defn how-many-neighbours-on-fire?
 "Returns the number of fires in the neighbour cells.
  It also works for indices out of scope. This may be useful for algorithms that extend the map/texture area." 
  [mat row col]
  (if (contains-fire? mat)
   (loop [i 0, fire-sum 0]
     (if (neighbour-on-fire? mat row col (nth map-utils/neighb-keys i))
       (if (< i 7)
         (recur (inc i) (inc fire-sum))
         fire-sum)
       (if (< i 7)
         (recur (inc i) fire-sum)
         fire-sum)))
    0))

;; 0 -> 0 or 2
;; 1 -> 0
;; 2 -> 2 or 1
(defn next-cell-value 
  "Returns the value of the cell of the next map iteration"
  [mat row col  forest-prob fire-prob]
  (if (= (map-utils/get-cell mat row col) fire)
    barren ; if burning then barren
    (if (burn? (map-utils/get-cell mat row col) fire-prob)
      fire ; else if probably start to burn (if tree inclusive)
      (if (grow? (map-utils/get-cell mat row col) forest-prob)
        tree ; else if probably start to grow (if barren inclusive)
        (map-utils/get-cell mat row col)))) ; else just stay the same
  ) 

(defn transform-cell-in-mat
  "Transformes one cell in the given matrix"
  [mat row col forest-prob fire-prob] 
  (assoc mat row (assoc (-> mat (nth row)) col (next-cell-value mat row col forest-prob fire-prob)))
  ) ; assoc=replace vector index new_val

;; (defn transform-mat
;;   [mat]
;;   (for [row mat]
;;    (for [cell row]
;;     (transform-cell-in-mat ))))

; better recursive

(defn transform-mat-recur
  [matrix first-row first-col forest-prob fire-prob]
  (loop [mat matrix 
         row first-row 
         col first-col]
       (if (has-next-index? mat row col)
         (recur (transform-cell-in-mat mat row col forest-prob fire-prob) ;; transform current cell and get new mat
                ((map-utils/get-next-index mat row col) :row) ;; next row
                ((map-utils/get-next-index mat row col) :col)) ;; next col) 
         (transform-cell-in-mat mat row col forest-prob fire-prob);; last cell
         )
  ))

(defn forest-fire-one-round 
  [matrix forest-probability fire-probability]
  (transform-mat-recur matrix 0 0 forest-probability fire-probability))

;; non-pure because of random|probabilistic behaviour
;; TODO and always wrap deterministic function with probabilistic behaviour function
(defn run-forest-fire-main
  [matrix, forest-probability, fire-probability, number-of-rounds]
  (loop [mat matrix
         forest-prob forest-probability
         fire-prob fire-probability
         rounds number-of-rounds]
    (let [next-mat (forest-fire-one-round mat forest-prob fire-prob)]
      (if (> rounds 1)
        (do (println rounds)
            (println next-mat) ;; if true (recur)
            (recur next-mat forest-prob fire-prob (dec rounds)))
        (do (println rounds)
            (forest-fire-one-round mat forest-prob fire-prob)) ;; if false (last round) 
        ))))

;; TODO meta goal
;; # pure function
(defn deterministic-forest-fire [mat]
  ())
