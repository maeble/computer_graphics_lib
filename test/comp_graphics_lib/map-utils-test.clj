(ns comp-graphics-lib.map-utils-test
  (:require [clojure.test :refer :all]
            [comp-graphics-lib.core :refer :all]
            [comp-graphics-lib.texture-generation.map-utils :as mu]
            [comp-graphics-lib.texture-generation.forest-fire :as ff]))

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

;; TODO
;; (deftest get-data-dict-test
;;   (testing "" (is ())))
;; ;; TESTS
;; (mu/get-data-dict test-mat-1 0 0) ; dict
;; (mu/get-data-dict test-mat-1 -1 0) ; nil

;; (deftest get-neighbours-test
;;   (testing "" (is ())))
;; ;; TESTS
;; (mu/get-neighbours test-map-neighbours 1 1) ; valid: all
;; (mu/get-neighbours test-map-neighbours 0 0) ; valid: 5,7,8

;; (deftest get-neighbour-of-test
;;   (testing "" (is ())))
;; ;; TESTS
;; (mu/get-neighbour-of test-map-neighbours 1 1 :1) ; :val=1
;; (mu/get-neighbour-of test-map-neighbours 1 1 :2) ; :val=2
;; (mu/get-neighbour-of test-map-neighbours 1 1 :3) ; :val=3
;; (mu/get-neighbour-of test-map-neighbours 1 1 :4) ; :val=4
;; (mu/get-neighbour-of test-map-neighbours 1 1 :5) ; :val=5
;; (mu/get-neighbour-of test-map-neighbours 1 1 :6) ; :val=6
;; (mu/get-neighbour-of test-map-neighbours 1 1 :7) ; :val=7 
;; (mu/get-neighbour-of test-map-neighbours 1 1 :8) ; :val=8
;; (mu/get-neighbour-of test-map-neighbours 0 0 :1) ; nil

;; (deftest get-next-index-test
;;   (testing "" (is ())))
;; ;; TESTS
;; (mu/get-next-index test-mat-1 0 0) ; mat 0 1
;; (mu/get-next-index test-mat-1 0 2) ; mat 1 0
;; (mu/get-next-index test-mat-1 2 2) ; nil
