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

(def fire-probability 0.05)
(def forest-probability 0.1)
(def tree 2)
(def fire 1)
(def barren 0)
(def mat [[tree tree tree] 
          [tree fire tree]
          [tree barren tree]])

;; code ==================================================

(defn burn?
  "Returns true if the cell should start burning.
   Returns false otherwise."
  [cell probability]
  (if (= cell tree)
    (> probability (rand))
    false))
;; TESTS
(burn? 2 fire-probability) ; test

(defn grow?
  "Returns true if the cell should grow a tree.
   Returns false otherwise."
  [cell probability]
  (if (= cell barren)
    (> probability (rand))
    false))
;; TESTS
(grow? 0 forest-probability) ; test 

(defn on-fire? [cell]
  (= cell fire))
;; TESTS
(on-fire? 0) ; F
(on-fire? 1) ; T
(on-fire? 2) ; F

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
;; TESTS
(contains-fire? mat) ; T
(contains-fire? [[0 1 0]]) ; T
(contains-fire? [[]]) ; F
(contains-fire? [[0 0 0]
                 [0 0 2]]) ; F

(defn neighbour-on-fire? 
  "Returns true if the neighbour cell of the given key is on fire."
  [mat row col neighb]
  (if (nil? ((map-utils/get-neighbours mat row col) neighb))
    false
    (= (((map-utils/get-neighbours mat row col) neighb) :val) fire)))
;; TESTS
(neighbour-on-fire? mat 0 0 :5) ; F
(neighbour-on-fire? mat 0 0 :8) ; T
(neighbour-on-fire? mat 0 0 :1) ; does not exist; F

(defn has-next-index?
  "Returns true if get-next-index can find a next cell"
  [mat row col] 
  (not (nil? (map-utils/get-next-index mat row col))
  ))
;; TESTS
(has-next-index? mat 0 0) ; true
(has-next-index? mat 1 2) ; true
(has-next-index? mat 2 2) ; false


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
;; TESTS
(def test-mat [[1 1 1]
               [1 0 2]
               [1 2 0]])
(how-many-neighbours-on-fire? test-mat -1 1) ; 2
(how-many-neighbours-on-fire? test-mat 2 2) ; 0
(how-many-neighbours-on-fire? test-mat 1 1) ; 5

;; 0 -> 0 or 2
;; 1 -> 0
;; 2 -> 2 or 1
(defn next-cell-value 
  "Returns the value of the cell of the next map iteration"
  [mat row col]
  (if (= (map-utils/get-cell mat row col) fire)
    barren ; if burning then barren
    (if (burn? (map-utils/get-cell mat row col) fire-probability)
      fire ; else if probably start to burn (if tree inclusive)
      (if (grow? (map-utils/get-cell mat row col) forest-probability)
        tree ; else if probably start to grow (if barren inclusive)
        (map-utils/get-cell mat row col)))) ; else just stay the same
  ) 
;; TESTS
(next-cell-value test-mat 0 0) ; 0
(next-cell-value test-mat 1 1) ; 0 or 2
(next-cell-value test-mat 2 1) ; 1 or 2


(defn transform-cell-in-mat
  "Transformes one cell in the given matrix"
  [mat row col] 
  (assoc mat row (assoc (-> mat (nth row)) col (next-cell-value mat row col)))
  ) ; assoc=replace vector index new_val
;; TESTS
(transform-cell-in-mat test-mat 0 0) ; [0 1 1] [1 0 2] [1 2 0]

;; (defn transform-mat
;;   [mat]
;;   (for [row mat]
;;    (for [cell row]
;;     (transform-cell-in-mat ))))

; better recursive

(defn transform-mat-recur
  [matrix first_row first_col]
  (loop [mat matrix 
         row first_row 
         col first_col]
       (if (has-next-index? mat row col)
         (recur (transform-cell-in-mat mat row col) ;; transform current cell and get new mat
                ((map-utils/get-next-index mat row col) :row) ;; next row
                ((map-utils/get-next-index mat row col) :col)) ;; next col) 
         (transform-cell-in-mat mat row col);; last cell
                )
  ))

(defn forest-fire-one-round 
  [matrix]
  (transform-mat-recur matrix 0 0))

;; TESTS
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
(forest-fire-one-round test-mat) ; [[0 0 0] [0 0/2 2] [0 2 0/2]]
(forest-fire-one-round big-test-mat)


;; TODO meta goal
;; # pure function
(defn deterministic-forest-fire [mat]
  ())

;; TODO meta goal
;; non-pure because of random|probabilistic behaviour
(defn run-forest-fire 
  [matrix, forest-probability, fire-probability, number-of-rounds] 
  ()
  )

