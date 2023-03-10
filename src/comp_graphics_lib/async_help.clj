(ns comp-graphics-lib.async-help
  (:require [clojure.core.async :as async]
            [clojail.core :as jail]))

(defmacro with-timeout [time & body]
  `(jail/thunk-timeout (fn [] ~@body) ~time))

;; Note: I am having problems getting async/go to work with Leiningen. 
;; (throws exception, probably correlated with aot compilation order as the ioc-macros namespace is dynaloaded). 
;; That's why this is implemented in such a unusual manner.
(defn collect-data [out timeout]
  (async/thread
    (loop [coll []]
      (let [value (try (with-timeout timeout (async/<!! out)) ;; <!! blocks until next value
                       (catch Exception _ nil))]
        (if (nil? value)
          coll
          (recur (conj coll value)))))))
