(ns kur.blog.post-test
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]
            [clojure.test :refer [is deftest testing run-tests]]
            [kur.blog.post :refer :all :as post]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :refer [for-all] :rename {for-all defp}]))

(deftest file-info-regression-test
  (is (= (file-info "not-exists") {})))

(defspec fname-parts-roundtrip-test 1000
  (defp [parts (s/gen ::post/file-name-parts)]
    (= parts (fname->parts (parts->fname parts)))))