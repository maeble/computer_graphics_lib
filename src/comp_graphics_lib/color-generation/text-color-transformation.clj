(ns comp-graphics-lib.color-generation.text-color-transformation)


(defn char-to-ascii [c]
  (let [color-range 256
        modulo-cut 111]
    (int (* (mod (int c) modulo-cut) (/ (dec color-range) (dec modulo-cut))))))
; at first I set the modulo to 256 because it is both the ascii and the color range. 
; but because all texts would average now ton appriximately the same color in different shades,
; I decided to change the modulo border to approximately the center of the small characters and scale it up
; now, the behaviour is better, but of course not an optimal, randomized distribution. 
; For this, I would need to hash the string. But this would make the algorithm much simpler and I would not 
; be able to try all this methods out.

(defn take-pruned-average [& values]
  (let [all (apply concat values)]
    (int (/ (reduce + all) (count all))))) ; for the + operation, apply and reduce are equivalent btw 

(defn transpose [coll]
  (apply map vector coll))

(defn round-up [d]
  (let [delta (- d (int d))]
    (if (< delta 0)
      (int d)
      (inc (int d)))))

(defn take-data [n data]
  (let [token (vec (take n data))]
    (if (< (count token) n)
      (vec (concat token (vec (repeat (- n (count token)) (last token)))))
      token
      )
    )
  )

(defn reshape [v x y] 
  (loop [data v
         resmat []]
    (let [remaining (take-last (- (count data) x) data)
          iter-res (conj resmat (take-data x data))]
      (if (> (count remaining) 0)
        (recur remaining iter-res)
        iter-res))))


(defn reshape-to-texture-map [rgb-list]
  (let [x (round-up (Math/sqrt (count rgb-list)))]
    (prn x)
    (reshape rgb-list x x)))


(defn strings-to-asciis [& text] ;; lazy sequence with for loop
  (let [entire-text (apply str text)]
    (vec (for [c (char-array entire-text)] (char-to-ascii c)))) ;; doseq is less appropriate here as it returns nil, but for returns a lazy sequence 
  )

(def average-color-values ; partial
  (partial take-pruned-average [255]) ; with base value 255
  )

(defn get-average-rgb [r-vals,g-vals,b-vals]
  [(average-color-values r-vals) (average-color-values g-vals) (average-color-values b-vals)])

(defn asciis-color-transform [asciis, color-collection-fn]
  (loop [seq asciis
         reds []
         greens []
         blues []]
    (let [[r g b & remaining :as all] seq] ; destructuring with & and :as
      ;(prn all); = seq = current numbers in the iteration, cropped from the start by 3 letters in each iteration
      (if (> (count remaining) 0)
        (recur remaining (conj reds r) (conj greens g) (conj blues b)) ; tail recursion
        (color-collection-fn reds greens blues)))))

(defn asciis-to-color
  [coll]
  (asciis-color-transform coll get-average-rgb))


(defn get-rgb-values [r-vals,g-vals,b-vals]
  (map (fn [r g b] [r g b]) r-vals g-vals b-vals))

(defn get-rgb-values' [r-vals,g-vals,b-vals] ;; equivalent
  (transpose [r-vals g-vals b-vals]))


(defn asciis-to-colors
  [coll]
  (asciis-color-transform coll get-rgb-values))

(def strings-to-color
  (comp asciis-to-color strings-to-asciis)) ; composition 

(def strings-to-colors
  (comp asciis-to-colors strings-to-asciis))

(def strings-to-texture-map
  (comp reshape-to-texture-map strings-to-colors)
  )


;; TESTING

(strings-to-color "nice day here" "nice day it is today")

(strings-to-color "forest")
(strings-to-color "fire!")

(strings-to-colors "num is used to coerce a primitive Java number type such as int, float, long, double, etc., into its boxed version such as Float, Long, Double, etc. If given an existing boxed Number type, as opposed to a primitive number type, it will just return it as is.")
(strings-to-color "num is used to coerce a primitive Java number type such as int, float, long, double, etc., into its boxed version such as Float, Long, Double, etc. If given an existing boxed Number type, as opposed to a primitive number type, it will just return it as is.")

(strings-to-texture-map "num is used to coerce a primitive Java number type such as int, float, long, double, etc., into its boxed version such as Float, Long, Double, etc. If given an existing boxed Number type, as opposed to a primitive number type, it will just return it as is.")
