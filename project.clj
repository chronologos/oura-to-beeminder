(defproject reframe-ring-backend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.10.0"]
                 [ring/ring-core "1.8.0"]
                 [ring/ring-jetty-adapter "1.8.0"]
                 [ring-cors "0.1.13"]
                 [compojure "1.6.1"]
                 [clj-http "3.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [environ "1.2.0"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]]

  :plugins [[lein-environ "1.2.0"]]

  :profiles {
             :dev           [:project/dev :profiles/dev]
             :test          [:project/test :profiles/test]
             :repl          [:project/repl :profiles/repl]
             ;; only edit :profiles/* in profiles.clj
             :profiles/dev  {}
             :profiles/test {}
             :profiles/repl {}
             :project/dev   {:source-paths ["src"]
                             :dependencies []
                             :plugins      [[lein-pprint "1.3.2"]]}
             :project/test  {}
             :project/repl {}
             :uberjar {:aot :all}}

  :main ^:skip-aot reframe-ring-backend.core
  :repl-options {:init-ns reframe-ring-backend.core})