(defproject jobtech-taxonomy-api-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-http "3.10.0"]
                 [cheshire "5.9.0"]
                 [martian "0.1.10"]
                 [martian-clj-http "0.1.10"]
                 ]
  :main ^:skip-aot jobtech-taxonomy-api-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :kaocha {:dependencies [[lambdaisland/kaocha "0.0-554"]]}
             }
  :aliases {"kaocha" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner"]}
  )
