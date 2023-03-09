(ns comp_graphics_lib.color_generation.text_color_transformation
  (:require
   [clojure.core.async :as async]
   [comp-graphics-lib.async-help :as ashelp]))

(defn transform-char-to-color-number [c color-range modulo-cut]
  (int (* (mod (int c) modulo-cut) (/ (dec color-range) (dec modulo-cut)))))

(defn char-to-color-number ; produces a very colorful map
  "Transforms a char to an ascii value. It does not just take the ascii integer value associated with the char, 
   but tries to map chars a litte bit more randomly to the rgb color numeric range (0:255)."
  [c]
  (transform-char-to-color-number c 256 111))
; at first I set the modulo-cut to 256 because it is both the ascii and the color range. 
; but because all texts would average now ton appriximately the same color in different shades,
; I decided to change the modulo border to approximately the center of the small characters and scale it up
; now, the behaviour is better, but of course not an optimal, randomized distribution. 
; For this, I would need to hash the string. But this would make the algorithm much simpler and I would not 
; be able to try out all this functional programming techniques


(defn char-to-color-number' ; produces a rather dirty-colored, grey map; the more straightforward/less randomized approach
  [c]
  (transform-char-to-color-number c 256 256))


(defmacro within [range-expr] ; a < x < b 
  (list `and
        (list (second range-expr) (first range-expr) (nth range-expr 2)) ; < a x
        (list (nth range-expr 3) (nth range-expr 2) (nth range-expr 4)) ; < x b
        ))

(defn filter-chars [values] ;; filter
  (filter (fn [x] (within (97 <= (int x) < 123))) values))


(defn modify-char-list [chars] ;; does not change the resulting map a lot, of course. Just for demonstration.
  (map #(%1 %2)  ; % refers to an argument for the anonymous function literal, defined with the dispatch/reader macro #(...)
       (cycle [inc identity dec]) (map int chars)) ; cycle returns infinite lazy sequence of the items (in this case inc, identity and dec)
  )

(defn filter-chars' [values] ;; pipeline implementation of "filter-chars" function
  (let [thread-count 4
        out (async/chan 1)
        in (async/chan 1)]
    (doseq [i values] (async/put! in i)) ; input
    (async/pipeline thread-count out (filter (fn [x] (within (97 <= (int x) < 123)))) in)
    (async/<!! (ashelp/collect-data out))))

;; (filter-chars' [ 1 2 6 100 103 7])

(defn preprocess-string [text]
  (apply str
         (-> text .toLowerCase char-array filter-chars to-array) ; enhance readability by reducing/simplifying nesting 
         ))

(defn take-pruned-average
  "Takes the average of all values and casts it to an integer."
  [& value-list]
  (let [all (apply concat value-list)]
    (int (/ (reduce + all) (count all))))) ; for the + operation, apply and reduce are equivalent btw 

(defn transpose
  "Transposes a matrix."
  [coll]
  (apply map vector coll))

(defn round-up
  "Always rounds up decimal values."
  [d]
  (let [delta (- d (int d))]
    (if (< delta 0)
      (int d)
      (inc (int d)))))


(defn take-data
  "If possible, takes n items from data. 
   If there are not enough values in data, it repeats the last value for filling up the output vector."
  [n data]
  (let [token (vec (take n data))]
    (if (< (count token) n)
      (vec (concat token (vec (repeat (- n (count token)) (last token)))))
      token)))

(defn reshape
  "Reshapes a flatten, non-empty vector v. 
   The items per row are defined by the integer x.
   The number of rows is then determinated by the number of items in v."
  [v x]
  (loop [data v
         resmat []]
    (let [remaining (take-last (- (count data) x) data)
          iter-res (conj resmat (take-data x data))]
      (if (> (count remaining) 0)
        (recur remaining iter-res)
        iter-res))))


(defn reshape-to-texture-map
  "Reshape the rgb-list to a matrix that is as square as possible."
  [rgb-list]
  (let [x (round-up (Math/sqrt (count rgb-list)))]
    (reshape rgb-list x)))


(defn strings-to-color-nums
  "Text is transformed to a vector of color numbers."
  [& text]
  (let [entire-text (apply str text)]
    (vec (for [c (modify-char-list (char-array entire-text))] (char-to-color-number' c)))) ;; doseq is less appropriate here as it returns nil, but for returns a lazy sequence 
  )

(def average-color-values ; adds to all color numbers the base color number 255
  (partial take-pruned-average [255]) ; with base value 255
  )

(defn average-color-values' ; the same with unquote splicing
  [values]
  (take-pruned-average `[255 ~@values]) ; 
  )

(defn get-average-rgb
  "Calculates the average rgb-value."
  [r-vals,g-vals,b-vals]
  [(average-color-values r-vals) (average-color-values g-vals) (average-color-values b-vals)])

(defn number-color-transform
  "Takes 3 sequential numbers as one rgb triple. 
   The color-collection-fn function defines, how all rgb triples shall be treated and returned (averaged, listed, etc)."
  [numbers, color-collection-fn]
  (loop [seq numbers
         reds []
         greens []
         blues []]
    (let [[r g b & remaining :as all] seq] ; destructuring with & and :as
      ;(prn all); = seq = current numbers in the iteration, cropped from the start by 3 letters in each iteration
      (if (> (count remaining) 0)
        (recur remaining (conj reds r) (conj greens g) (conj blues b)) ; tail recursion
        (color-collection-fn reds greens blues)))))

(defn numbers-to-color
  "Transforms color numbers to one rgb color."
  [coll]
  (number-color-transform coll get-average-rgb))


(defn get-rgb-values
  "Collects all rgb values in a list."
  [r-vals,g-vals,b-vals]
  (map (fn [r g b] [r g b]) r-vals g-vals b-vals))

(defn get-rgb-values' [r-vals,g-vals,b-vals] ;; equivalent
  (transpose [r-vals g-vals b-vals]))


(defn numbers-to-colors
  "Transforms color numbers to a list of rgb colors."
  [coll]
  (number-color-transform coll get-rgb-values))

(def strings-to-color
  (comp numbers-to-color strings-to-color-nums)) ; composition 

(def strings-to-colors
  (comp numbers-to-colors strings-to-color-nums)) ; composition 

(def strings-to-texture-map
  (comp reshape-to-texture-map strings-to-colors))


;; TESTING
;; (def input_string "num is used to coerce a primitive Java number type such as int, float, long, double, etc., into its boxed version such as Float, Long, Double, etc. If given an existing boxed Number type, as opposed to a primitive number type, it will just return it as is!")

;; (strings-to-color input_string)
;; (strings-to-color (preprocess-string input_string))

;; (strings-to-colors input_string)
;; (strings-to-colors (preprocess-string input_string))

;; (strings-to-texture-map input_string)
;; (strings-to-texture-map (preprocess-string input_string))
