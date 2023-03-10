(defproject comp-graphics-lib "0.1.0-SNAPSHOT"
  :description "texture generation algorithm demonstration in clojure"
  :url "https://gitlab.mi.hdm-stuttgart.de/mb342/computer-graphics-lib"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cljfx "1.7.22"]
                 [org.clojure/core.match "1.0.1"]
                 [org.openjfx/javafx-controls "17.0.1"]
                 [org.clojure/core.async "1.6.673"]
                 [clojail "1.0.6"]]
  :main ^:skip-aot comp_graphics_lib.core
  :aot-exclude-ns [cljs.core.async.macros
                   cljs.core.impl-ioc-macros]
  :target-path "target/%s"
  :profiles {:uberjar
             {:aot :all
              :injections [(javafx.application.Platform/exit)
                           (prn "Done")]}
             :repl {:injections [(javafx.application.Platform/exit)
                                 (prn "Done")]}}
  :test-path "test/comp_graphics_lib"
  :prep-tasks [["compile" "comp_graphics_lib.texture_generation.map_utils"]
               ["compile" "comp_graphics_lib.texture_generation.forest_fire"]
               ["compile" "comp_graphics_lib.texture_generation.forest_fire_ui"]
               ["compile" "comp_graphics_lib.color_generation.text_color_transformation"]
               ["compile" "comp_graphics_lib.color_generation.text_to_color_ui"]
               ["compile" "comp_graphics_lib.core"]
               ["javac"]])
