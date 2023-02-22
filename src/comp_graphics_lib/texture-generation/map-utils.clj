(ns comp-graphics-lib.texture-generation.map-utils)


(def neighb-keys (list :1 :2 :3 :4 :5 :6 :7 :8))


(defn cell-exists?
  "Returns true if the cell index exists in the map matrix.
   Returns false otherwise."
  [mat row col]
  (and (contains? mat row) (contains? (-> mat (nth row)) col)))


(defn get-cell-value
  "Returns the content of a map cell at a given index.
   Throws an IndexOutOfBounds exception if the index is invalid"
  [mat row col]
  (-> mat (nth row) (nth col)))

(defn get-cell-info [mat row col]
  (try [row col (get-cell-value mat row col)]
    (catch Exception _ nil))) ; if cell invalid, then return nil

(defn get-neighbours
  "Returns a dictionary of data dictionaries of all neighbours for a specific cell.
   Each cell has eight neighbours (except border cells):
   1 2 3 
   4 x 5
   6 7 8"
  [mat row col]
  {:1 (get-cell-info mat (dec row) (dec col))
   :2 (get-cell-info mat (dec row) col)
   :3 (get-cell-info mat (dec row) (inc col))
   :4 (get-cell-info mat row (dec col))
   :5 (get-cell-info mat row (inc col))
   :6 (get-cell-info mat (inc row) (dec col))
   :7 (get-cell-info mat (inc row) col)
   :8 (get-cell-info mat (inc row) (inc col))})


(defn get-neighbour-of
  "Returns the data-dictionary {row, column, value} of a neighbor at a specific position.
   Returns nil if the neighbour position is invalid."
  [mat row col neighbour]
  ((get-neighbours mat row col) neighbour))

(defn get-next-cell
  "Iterates row by row and left to right over the map."
  [mat row col]
  (if (cell-exists? mat row (inc col))
    (get-cell-info mat row (inc col))
    (if (cell-exists? mat (inc row) 0)
      (get-cell-info mat (inc row) 0)
      nil)))

(defn has-next-index?
  "Returns true if get-next-index can find a next cell"
  [mat row col]
  (not (nil? (get-next-cell mat row col))))


(defn weigted-random 
  "Selects a key from the distribution map via weigted random choice.
   The distribution map defines for each possible key a distribution weight.
   "
  [distribution-map] 
  (let [weights (vals distribution-map)
        total-weight (reduce + weights) ;; simple reduce
        r (rand total-weight)]
    (loop [i 0, sum 0]
      (if (< r (+ (get (vec weights) i) sum))
        (get (vec (keys distribution-map)) i)
        (recur (inc i) (+ (get (vec weights) i) sum))))))


;; Note: macro needed for "generate-random-map" as # are not allowed to be nested: 
;; "repeatedly rows #(vec...(..#..))" throws Exception
(defmacro repeatedly' [n & body]
  `(repeatedly ~n (fn [] ~@body)))


(defn generate-random-map 
  "Generates a random-weighted texture map based on the distribution map. 
   The distribution map defines for each possible cell value a distribution weight.
   The smaller the weight compared to the total weight of all possible cell values, 
   the rarer the value on the result map."
  [rows, cols, distribution-map] 
  (vec (repeatedly' rows (vec (repeatedly' cols (weigted-random distribution-map))))) 
  )
