(ns comp-graphics-lib.texture-generation.map-utils)

(def test-mat [[2 2 2]
          [2 1 2]
          [2 0 2]])

(defn cell-exists?
  "Returns true if the cell index exists in the map matrix.
   Returns false otherwise."
  [mat row col]
  (and (contains? mat row) (contains? (-> mat (nth row)) col)))
;; TESTS
(cell-exists? test-mat 2 2) ; T
(cell-exists? test-mat 2 3) ; F
(cell-exists? test-mat 3 2) ; F 
(cell-exists? test-mat 0 0) ; T
(cell-exists? test-mat -1 0) ; F
(cell-exists? test-mat 0 -1) ; F

(defn get-cell
  "Returns the content of a map cell at a given index.
   Throws an IndexOutOfBounds exception if the index is invalid"
  [mat row col]
  (-> mat (nth row) (nth col)))
;; TESTS
(get-cell test-mat 2 2) ; 2
(get-cell test-mat -1 0) ; IndexOutOfBounds
(get-cell test-mat 0 -1) ; IndexOutOfBounds


(defn get-data-dict [mat row col]
  (try {:row row :col col :val (get-cell mat row col)}
       (catch Exception _ nil)))
;; TESTS
(get-data-dict test-mat 0 0) ; dict
(get-data-dict test-mat -1 0) ; nil

(defn get-neighbours
  "Returns a dictionary of data dictionaries of all neighbours for a specific cell.
   Each cell has eight neighbours (except border cells):
   1 2 3 
   4 x 5
   6 7 8"
  [mat row col]
  {:1 (get-data-dict mat (dec row) (dec col))
   :2 (get-data-dict mat (dec row) col)
   :3 (get-data-dict mat (dec row) (inc col))
   :4 (get-data-dict mat row (dec col))
   :5 (get-data-dict mat row (inc col))
   :6 (get-data-dict mat (inc row) (dec col))
   :7 (get-data-dict mat (inc row) col)
   :8 (get-data-dict mat (inc row) (inc col))})
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

(defn get-next-index
  "Iterates row by row and left to right over the map."
  [mat row col]
  (if (cell-exists? mat row (inc col))
    (get-data-dict mat row (inc col))
    (if (cell-exists? mat (inc row) 0)
      (get-data-dict mat (inc row) 0)
      nil)))
;; TESTS
(get-next-index test-mat 0 0) ; mat 0 1
(get-next-index test-mat 0 2) ; mat 1 0
(get-next-index test-mat 2 2) ; nil
