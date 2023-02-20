(defproject comp-graphics-lib "0.1.0-SNAPSHOT"
  :description "texture generation algorithm demonstration in clojure"
  :url "https://gitlab.mi.hdm-stuttgart.de/mb342/computer-graphics-lib"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :resource-paths ["resources/humbleui-0.0.0.jar"]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cljfx "1.7.22"] 
                 ] 
  :main ^:skip-aot comp-graphics-lib.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
