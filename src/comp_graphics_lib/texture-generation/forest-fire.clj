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


;; configurations ========================================

(def fire-probability 0.005)
(def forest-probability 0.01)
(def tree 2)
(def fire 1)
(def barren 0)
(def mat [[tree tree tree]
          [tree fire tree]
          [tree barren tree]])

;; code ==================================================

;; TESTS
mat ; test

(defn burn?
  "Returns true if the cell should start burning.
   Returns false otherwise."
  [cell]
  (if (= cell tree)
    (> fire-probability (rand))
    false))
;; TESTS
(burn? 2) ; test

(defn grow?
  "Returns true if the cell should grow a tree.
   Returns false otherwise."
  [cell]
  (if (= cell barren)
    (> forest-probability (rand))
    false))
;; TESTS
(grow? 0) ; test 

(defn cell-exists?
  "Returns true if the cell index exists in the map matrix.
   Returns false otherwise."
  [mat row col]
  (and (contains? mat row) (contains? (-> mat (nth row)) col)))
;; TESTS
(cell-exists? mat 2 2) ; T
(cell-exists? mat 2 3) ; F
(cell-exists? mat 3 2) ; F 
(cell-exists? mat 0 0) ; T
(cell-exists? mat -1 0) ; F
(cell-exists? mat 0 -1) ; F

(defn get-cell
  "Returns the content of a map cell at a given index.
   Throws an IndexOutOfBounds exception if the index is invalid"
  [mat row col]
  (-> mat (nth row) (nth col)))
;; TESTS
(get-cell mat 2 2) ; 2
(get-cell mat -1 0) ; IndexOutOfBounds
(get-cell mat 0 -1) ; IndexOutOfBounds

(defn on-fire? [cell]
  (= cell 1))
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
         (_contains-fire? mat (+ row 1)))
       false))
   mat 0))
;; TESTS
(contains-fire? mat) ; T
(contains-fire? [[0 1 0]]) ; T
(contains-fire? [[]]) ; F
(contains-fire? [[0 0 0]
                 [0 0 2]]) ; F

(defn get-data-dict [mat row col]
  (try {:row row :col col :val (get-cell mat row col)}
       (catch Exception _ nil)))
;; TESTS
(get-data-dict mat 0 0) ; dict
(get-data-dict mat -1 0) ; nil

(defn get-neighbours
  "Returns a dictionary of data dictionaries of all neighbours for a specific cell.
   Each cell has eight neighbours (except border cells):
   1 2 3 
   4 x 5
   6 7 8"
  [mat row col]
  {:1 (get-data-dict mat (- row 1) (- col 1))
   :2 (get-data-dict mat (- row 1) col)
   :3 (get-data-dict mat (- row 1) (+ col 1))
   :4 (get-data-dict mat row (- col 1))
   :5 (get-data-dict mat row (+ col 1))
   :6 (get-data-dict mat (+ row 1) (- col 1))
   :7 (get-data-dict mat (+ row 1) col)
   :8 (get-data-dict mat (+ row 1) (+ col 1))})
;; TESTS
(def neighbourmap  [[1 2 3]
                    [4 0 5]
                    [6 7 8]])
(get-neighbours neighbourmap 1 1) ; valid: all
(get-neighbours neighbourmap 0 0) ; valid: 5,7,8

(def neighb-keys (list :1 :2 :3 :4 :5 :6 :7 :8))

(defn get-neighbour-of
  "Returns the data-dictionary {row, column, value} of a neighbor at a specific position.
   Returns nil if the neighbour position is invalid."
  [mat row col neighbour]
  ((get-neighbours mat row col) neighbour))
;; TESTS
(get-neighbour-of neighbourmap 1 1 :1) ; :val=1
(get-neighbour-of neighbourmap 1 1 :2) ; :val=2
(get-neighbour-of neighbourmap 1 1 :3) ; :val=3
(get-neighbour-of neighbourmap 1 1 :4) ; :val=4
(get-neighbour-of neighbourmap 1 1 :5) ; :val=5
(get-neighbour-of neighbourmap 1 1 :6) ; :val=6
(get-neighbour-of neighbourmap 1 1 :7) ; :val=7 
(get-neighbour-of neighbourmap 1 1 :8) ; :val=8
(get-neighbour-of neighbourmap 0 0 :1) ; nil

;; ;; TODO for each neighbour call action and continue
;; (defn for-each-neighbour [mat row col action]
;;   if (cell-exists? mat row col)
;;   ((seq (get-neighbours mat row col))
;;    nil))


(defn neighbour-on-fire? 
  "Returns true if the neighbour cell of the given key is on fire."
  [mat row col neighb]
  (if (nil? ((get-neighbours mat row col) neighb))
    false
    (= (((get-neighbours mat row col) neighb) :val) fire)))
;; TESTS
(neighbour-on-fire? mat 0 0 :5) ; F
(neighbour-on-fire? mat 0 0 :8) ; T
(neighbour-on-fire? mat 0 0 :1) ; does not exist; F


(defn get-next-index 
  "Iterates row by row and left to right over the map."
  [mat row col]
  (if (cell-exists? mat row (+ col 1))
    (get-data-dict mat row (+ col 1)) 
    (if (cell-exists? mat (+ row 1) 0)
      (get-data-dict mat (+ row 1) 0) 
      nil))) 
;; TESTS
(get-next-index mat 0 0) ; mat 0 1
(get-next-index mat 0 2) ; mat 1 0
(get-next-index mat 2 2) ; nil


(defn neighbours-on-fire?
 "Returns the number of fires in the neighbour cells.
  It also works for indices out of scope. This may be useful for algorithms that extend the map/texture area." 
  [mat row col]
  (if (contains-fire? mat)
   (loop [i 0, fire-sum 0]
     (if (neighbour-on-fire? mat row col (nth neighb-keys i))
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
(neighbours-on-fire? test-mat -1 1) ; 2
(neighbours-on-fire? test-mat 2 2) ; 0
(neighbours-on-fire? test-mat 1 1) ; 5



;; TODO meta goal
;; # pure function
(defn deterministic-forest-fire [mat]
  ())

;; non-pure because of random|probabilistic behaviour
;; (defn forest-fire [input_matrix, forest-probability, fire-probability] ())

