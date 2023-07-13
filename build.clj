(ns build
  (:require [clojure.tools.build.api :as b]
            [clojure.java.shell :as sh]))

(def lib 'net.cgrand/macrovich)
(def version "0.2.2"  #_(format "0.0.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn clojars [_]
  (sh/sh
    "mvn" "deploy:deploy-file" (str "-Dfile=" jar-file)
    (format "-DpomFile=%s/META-INF/maven/%s/%s/pom.xml"
      class-dir (namespace lib) (name lib))
    "-DrepositoryId=clojars" "-Durl=https://clojars.org/repo/"))
