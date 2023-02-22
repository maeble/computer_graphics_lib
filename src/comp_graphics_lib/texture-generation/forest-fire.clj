(ns comp-graphics-lib.texture-generation.forest-fire
  (:require
   [comp-graphics-lib.texture-generation.map-utils :as map-utils]
   [clojure.core.match :as match]
   ) 
  )

;; configurations ========================================

(def tree 2)
(def fire 1)
(def barren 0)

;; code ==================================================

(defn burn?
  "(Randomly) returns true if the cell should start burning.
   This refers to the base probability of each forest cell to start burning."
  [cell probability]
  (if (= cell tree)
    (> probability (rand))
    false))

(defn catch-fire? 
  "(Randomly) returns true if the cell should start burning, 
   weighted by the number of burning neighbors."
  [cell num-of-neighbours-on-fire] 
  (if (= cell tree)
    (> (* (/ num-of-neighbours-on-fire 8) 2.5) (rand)) ;; 8 = number of neighbours per cell, 2.5 = weight to make fire more likely in general
    false)
  )

(defn grow?
  "(Randomly) returns true if the cell should grow a tree."
  [cell probability]
  (if (= cell barren)
    (> probability (rand))
    false))

(defn on-fire? 
  "Returns true, if the given cell contains fire"
  [cell-value] 
  (= cell-value fire))

; Clj can use java functions with .javaFuncName 
; just for demonstration - could also use clojure-function here
(defn contains-fire?
  "Returns true if the map matrix has a burning cell."
  [mat]
  (if (.contains (flatten mat) fire)
    true
    false)
)

;; sequential destructuring of neighbor-info
(defn neighbour-on-fire?
  "Returns true if the neighbour cell of the given key is on fire."
  [mat row col neighb]
  (let [neighbor-info (map-utils/get-neighbour-of mat row col neighb)]
    (if (nil? neighbor-info)
      false
      (let [[_ _ value] neighbor-info]
        (on-fire? value))))
  )

;; with recur
(defn how-many-neighbours-on-fire?'
 "Returns the number of fires in the neighbour cells.
  It also works for indices out of scope. 
  This may be useful for an algorithm that extends the map/texture area." 
  [mat row col]
  (if (contains-fire? mat)
   (loop [i 0, fire-sum 0]
     (if (< i 8)
       (if (neighbour-on-fire? mat row col (nth map-utils/neighb-keys i))
         (recur (inc i) (inc fire-sum))
         (recur (inc i) fire-sum))
       fire-sum))
    0))

;; with reduce: reduce [reduction function] [initial value] [collection to reduce]
(defn how-many-neighbours-on-fire?
  "Returns the number of fires in the neighbour cells.
  It also works for indices out of scope. 
  This may be useful for an algorithm that extends the map/texture area."
  [mat row col]
  (if (contains-fire? mat)
    (reduce (fn [sum neighb-key] (if (neighbour-on-fire? mat row col neighb-key) (inc sum) sum)) ; anonymous reduction fn
            0 (keys (map-utils/get-neighbours mat row col)))
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

;; (simple) pattern matching: way more readable
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
         (let [[next-row next-col] (map-utils/get-next-cell new-mat row col)] ;; sequential destructuring
           (recur (transform-cell-in-mat matrix new-mat row col forest-prob fire-prob) ;; transform current cell and get new mat
                  next-row
                  next-col)
           ) 
         (transform-cell-in-mat matrix new-mat row col forest-prob fire-prob);; last cell
         )
  ))

;; wrapper function for 
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
