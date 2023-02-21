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
   [comp-graphics-lib.texture-generation.map-utils :as map-utils]
   [clojure.core.match :as match]
   ) 
  (:import )
  )


;; configurations ========================================

(def tree 2)
(def fire 1)
(def barren 0)

;; code ==================================================

(defn burn?
  "(Randomly) returns true if the cell should start burning .
   Returns false otherwise."
  [cell probability]
  (if (= cell tree)
    (> probability (rand))
    false))

(defn catch-fire? 
  "(Randomly) returns true if the cell should start burning, 
   weighted by the number of burning neighbors."
  [cell num-of-neighbours-on-fire] 
  (if (= cell tree)
    (> (* (/ num-of-neighbours-on-fire 8) 2.5) (rand))
    false)
  )

(defn grow?
  "(Randomly) returns true if the cell should grow a tree.
   Returns false otherwise."
  [cell probability]
  (if (= cell barren)
    (> probability (rand))
    false))

(defn on-fire? 
  "Returns true, if the given cell contains fire"
  [cell] 
  (= cell fire))

; Clj can use java functions with .javaFuncName 
; here just for demonstration - could also use clojure-function "contains?"
(defn contains-fire?
  "Returns true if the map matrix has a burning cell.
   Returns false otherwise."
  [mat]
  (if (.contains (flatten mat) fire)
    true
    false)
)

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
     (if (< i 7)
       (if (neighbour-on-fire? mat row col (nth map-utils/neighb-keys i))
         (recur (inc i) (inc fire-sum))
         (recur (inc i) fire-sum)
         )
       fire-sum
       ))
    0))

;; no pattern matching
(defn next-cell-value' 
  "Returns the value of the cell of the next map iteration. \n
   Pattern: \n
   0 -> 0 or 2 \n
   1 -> 0 \n
   2 -> 2 or 1 \n
   "
  [mat row col  forest-prob fire-prob]
  (let [cell (map-utils/get-cell-value mat row col)]
    (if (= cell fire)
      barren ; if burning then barren
      (if (or (burn? cell fire-prob) (catch-fire? cell (how-many-neighbours-on-fire? mat row col)))
        fire ; else if probably start to burn (if tree inclusive)
        (if (grow? cell forest-prob)
          tree ; else if probably start to grow (if barren inclusive)
          cell))) ; else just stay the same 
    ) 
  ) 

;; pattern matching: way more readable
(defn next-cell-value
  "Returns the value of the cell of the next map iteration. \n
   Pattern: \n
   0 -> 0 or 2 \n
   1 -> 0 \n
   2 -> 2 or 1 \n
   "  
  [mat row col forest-prob fire-prob]
  (let [cell (map-utils/get-cell-value mat row col)
        fire-key fire
        tree-key tree
        barren-key barren
        ]
    (match/match [cell]
      [fire-key] barren
      [barren-key] (if (grow? cell forest-prob)
                     tree  
                     cell) 
      [tree-key] (if (or (burn? cell fire-prob) (catch-fire? cell (how-many-neighbours-on-fire? mat row col)))
                     fire 
                     cell) 
      [other] other
      ) 
    ))

(defn transform-cell-in-mat
  "Transformes one cell in the given matrix"
  [mat new-mat row col forest-prob fire-prob] 
  (assoc new-mat row (assoc (-> new-mat (nth row)) col (next-cell-value mat row col forest-prob fire-prob)))
  ) ; assoc=replace vector index new_val


;; recursive - could have also been implemented with nested for loop
(defn transform-mat-recur
  [matrix first-row first-col forest-prob fire-prob]
  (loop [new-mat matrix 
         row first-row 
         col first-col]
       (if (map-utils/has-next-index? new-mat row col)
         (let [next-index (map-utils/get-next-index new-mat row col)]
           (recur (transform-cell-in-mat matrix new-mat row col forest-prob fire-prob) ;; transform current cell and get new mat
                  (next-index :row)
                  (next-index :col))
           ) 
         (transform-cell-in-mat matrix new-mat row col forest-prob fire-prob);; last cell
         )
  ))

(defn run-forest-fire-one-round 
  "Runs the forest fire map transformation for one iteration and returns the resulting map."
  [matrix forest-probability fire-probability]
  (transform-mat-recur matrix 0 0 forest-probability fire-probability))

;; non-pure because of random|probabilistic behaviour
;; TODO and always wrap deterministic function with probabilistic behaviour function
(defn run-forest-fire-main
  "Runs the forest fire map transformation for [number-of-rounds] iterations and returns the
   resulting map."
  [matrix, forest-probability, fire-probability, number-of-rounds]
  (loop [mat matrix
         forest-prob forest-probability
         fire-prob fire-probability
         rounds number-of-rounds]
    (let [next-mat (run-forest-fire-one-round mat forest-prob fire-prob)]
      (if (> rounds 1)
        (recur next-mat forest-prob fire-prob (dec rounds))
        (run-forest-fire-one-round mat forest-prob fire-prob)
        ))))

(defn get-new-random-map 
  "Returns a new forest fire map with [rows] rows amd [cols] columns.
   The forest-, fire- and barren-weight define how often the values shall appear 
   relatively to the total weight (weighted random distribution).
   "
  [rows, cols, forest-weight, fire-weight, barren-weight] 
  (map-utils/generate-random-map rows cols 
                                 {tree forest-weight, 
                                  fire fire-weight,
                                  barren barren-weight
                                  }))
