(ns comp-graphics-lib.texture-generation.map-utils)


(def neighb-keys (list :1 :2 :3 :4 :5 :6 :7 :8))


(defn cell-exists?
  "Returns true if the cell index exists in the map matrix.
   Returns false otherwise."
  [mat row col]
  (and (contains? mat row) (contains? (-> mat (nth row)) col)))


(defn get-cell
  "Returns the content of a map cell at a given index.
   Throws an IndexOutOfBounds exception if the index is invalid"
  [mat row col]
  (-> mat (nth row) (nth col)))


(defn get-data-dict [mat row col]
  (try {:row row :col col :val (get-cell mat row col)}
       (catch Exception _ nil)))


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


(defn get-neighbour-of
  "Returns the data-dictionary {row, column, value} of a neighbor at a specific position.
   Returns nil if the neighbour position is invalid."
  [mat row col neighbour]
  ((get-neighbours mat row col) neighbour))


(defn get-next-index
  "Iterates row by row and left to right over the map."
  [mat row col]
  (if (cell-exists? mat row (inc col))
    (get-data-dict mat row (inc col))
    (if (cell-exists? mat (inc row) 0)
      (get-data-dict mat (inc row) 0)
      nil)))
