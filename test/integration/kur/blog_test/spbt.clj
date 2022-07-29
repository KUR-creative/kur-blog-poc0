(ns kur.blog-test.spbt
  "Blog Integration Test Using Stateful PBT"
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as g]
   [kur.blog.post :as post]
   [kur.util.generator :refer [string-from-regexes]]
   [kur.util.regex :refer [ascii* common-whitespace* hangul*]]
   ))

;; Generators and Specs
(s/def ::md-text
  (s/with-gen string?
    #(string-from-regexes ascii* common-whitespace* hangul*)))

(s/def ::post (s/keys :req [::post/public? ::post/title ::md-text]))
(s/def ::state (s/map-of ::post/id ::post))

;;
(comment
  #_(require '[babashka.fs :as fs]
           '[clojure.string :as str])
  #_(str/join " " ;; To know used characters
              (->> (fs/list-dir "/home/dev/outer-brain/thinks/")
                   (map #(-> % str slurp set))
                   (apply clojure.set/union) (sort)))
  (g/sample (s/gen ::md-text) 30)
  (g/sample (s/gen ::post) 30)
  (g/sample (s/gen ::state) 10)
  )