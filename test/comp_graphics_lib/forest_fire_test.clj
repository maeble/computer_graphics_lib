(ns comp_graphics_lib.forest_fire_test
  (:require [clojure.test :refer :all]
            [comp_graphics_lib.texture_generation.forest_fire :as ff]))

(def test-mat-1 [[ff/tree ff/tree ff/tree]
                 [ff/tree ff/fire ff/tree]
                 [ff/tree ff/barren ff/tree]])

(def test-mat-2 [[ff/fire ff/fire ff/fire]
                 [ff/fire ff/barren ff/tree]
                 [ff/fire ff/tree ff/barren]])

(deftest on-fire?-test
  (testing "valid input" (is (false? (ff/on-fire? ff/barren))))
  (testing "valid input" (is (false? (ff/on-fire? ff/tree))))
  (testing "valid input" (is (ff/on-fire? ff/fire)))
  (testing "invalid input" (is (false? (ff/on-fire? -1))))
  )

(on-fire?-test)

(deftest contains-fire?-test
  (testing "matrix" (is (ff/contains-fire? test-mat-1)))
  (testing "first-row" (is (ff/contains-fire? [[0 1 0]])))
  (testing "empty matrix" (is (false? (ff/contains-fire? [[]]))))
  (testing "no fire" (is (false? (ff/contains-fire? [[0 0 0]
                                                     [0 0 2]]))))
  )
(contains-fire?-test)

(deftest neighbour-on-fire?-test
  (testing "no neighbour on fire" (is (false? (ff/neighbour-on-fire? test-mat-1 0 0 :5))))
  (testing "neighbour on fire" (is (true? (ff/neighbour-on-fire? test-mat-1 0 0 :8))))
  (testing "neighbour does not exist" (is (false? (ff/neighbour-on-fire? test-mat-1 0 0 :4))))
  )
(neighbour-on-fire?-test)


(deftest how-many-neighbours-on-fire?-test
  (testing "invalid, off-map cell" (is (= 3 (ff/how-many-neighbours-on-fire? test-mat-2 1 -1))))
  (testing "invalid, off-map cell" (is (= 3 (ff/how-many-neighbours-on-fire? test-mat-2 -1 1))))
  (testing "no neighbours on fire" (is (= 0 (ff/how-many-neighbours-on-fire? test-mat-2 2 2))))
  (testing "neighbours on fire" (is (= 5 (ff/how-many-neighbours-on-fire? test-mat-2 1 1)))))
(how-many-neighbours-on-fire?-test)

(deftest how-many-neighbours-on-fire?'-test
  (testing "invalid, off-map cell" (is (= 3 (ff/how-many-neighbours-on-fire?' test-mat-2 1 -1))))
  (testing "invalid, off-map cell" (is (= 3 (ff/how-many-neighbours-on-fire?' test-mat-2 -1 1))))
  (testing "no neighbours on fire" (is (= 0 (ff/how-many-neighbours-on-fire?' test-mat-2 2 2))))
  (testing "neighbours on fire" (is (= 5 (ff/how-many-neighbours-on-fire?' test-mat-2 1 1))))
  )
(how-many-neighbours-on-fire?'-test)

(deftest next-cell-value-test
  (testing "fire" (is (= 0 (ff/next-cell-value test-mat-2 0 0 0.05 0.05))))
  (testing "barren" (is (or (= 0 (ff/next-cell-value test-mat-2 1 1 0.05 0.05))
                            (is (= 2 (ff/next-cell-value test-mat-2 1 1 0.05 0.05))))))
  (testing "tree" (is (or (= 1 (ff/next-cell-value test-mat-2 2 1 0.05 0.05))
                          (= 2 (ff/next-cell-value test-mat-2 2 1 0.05 0.05))))))
(next-cell-value-test)

;; (deftest -test
;;   (testing "" (is ())))
