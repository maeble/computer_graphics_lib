(ns comp-graphics-lib.map-utils-test
  (:require [clojure.test :refer :all]
            [comp_graphics_lib.texture_generation.map_utils :as mu]
            [comp_graphics_lib.texture_generation.forest_fire :as ff]))

(def test-mat-1 [[ff/tree ff/tree ff/tree]
                 [ff/tree ff/fire ff/tree]
                 [ff/tree ff/barren ff/tree]])

(def test-map-neighbours  [[1 2 3]
                           [4 0 5]
                           [6 7 8]])

(deftest cell-exists?-test
  (testing "cell exists" (is (true? (mu/cell-exists? test-mat-1 2 2))))
  (testing "cell out of columns" (is (false? (mu/cell-exists? test-mat-1 2 3))))
  (testing "cell out of rows" (is (false? (mu/cell-exists? test-mat-1 3 2))))
  (testing "first map cell exists" (is (true? (mu/cell-exists? test-mat-1 0 0))))
  (testing "invalid row index" (is (false? (mu/cell-exists? test-mat-1 -1 0))))
  (testing "invalid column index" (is (false? (mu/cell-exists? test-mat-1 0 -1))))
  )
(cell-exists?-test)

(deftest get-cell-test
  (testing "cell exists" (is (= 2 (mu/get-cell-value test-mat-1 2 2))))
  (testing "invalid row index" (is (thrown? IndexOutOfBoundsException (mu/get-cell-value test-mat-1 -1 0))))
  (testing "invalid column index" (is (thrown? IndexOutOfBoundsException (mu/get-cell-value test-mat-1 0 -1))))
  )

(get-cell-test)

(deftest has-next-index?-test
  (testing "" (is (true? (mu/has-next-index? test-mat-1 0 0))))
  (testing "" (is (true? (mu/has-next-index? test-mat-1 1 2))))
  (testing "" (is (false? (mu/has-next-index? test-mat-1 2 2)))))
(has-next-index?-test)


(deftest get-data-dict-test
  (testing "normal" (is (false? (nil? (mu/get-cell-info test-mat-1 0 0)))))
  (testing "normal" (is (true? (nil? (mu/get-cell-info test-mat-1 -1 0)))))
  )

(deftest get-neighbour-of-test
  (let [[_ _ val] (mu/get-neighbour-of test-map-neighbours 1 1 :1)] (testing "1" (is (= 1 val))))
  (let [[_ _ val] (mu/get-neighbour-of test-map-neighbours 1 1 :2)] (testing "2" (is (= 2 val))))
  (let [[_ _ val] (mu/get-neighbour-of test-map-neighbours 1 1 :3)] (testing "3" (is (= 3 val))))
  (let [[_ _ val] (mu/get-neighbour-of test-map-neighbours 1 1 :4)] (testing "4" (is (= 4 val))))
  (let [[_ _ val] (mu/get-neighbour-of test-map-neighbours 1 1 :5)] (testing "5" (is (= 5 val))))
  (let [[_ _ val] (mu/get-neighbour-of test-map-neighbours 1 1 :6)] (testing "6" (is (= 6 val))))
  (let [[_ _ val] (mu/get-neighbour-of test-map-neighbours 1 1 :7)] (testing "7" (is (= 7 val))))
  (let [[_ _ val] (mu/get-neighbour-of test-map-neighbours 1 1 :8)] (testing "8" (is (= 8 val))))
  (let [[_ _ val] (mu/get-neighbour-of test-map-neighbours 0 0 :1)] (testing "nil" (is (nil? val))))
  )
