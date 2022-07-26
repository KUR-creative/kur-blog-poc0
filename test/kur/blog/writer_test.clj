(ns kur.blog.writer-test
  (:require [clojure.test :refer [deftest testing is run-tests]]
            [babashka.fs :as fs]))

(def md-dir "test/fixture/post-md/")
(def html-dir "test/fixture/post-html/")

(defn delete-all-except-gitkeep [dir]
  (->> (fs/list-dir dir)
       (remove #(= (fs/file-name %) ".gitkeep"))
       (run! fs/delete)))

(deftest create-test
  (delete-all-except-gitkeep md-dir)
  (delete-all-except-gitkeep html-dir)
  (testing "Add new post.md, then Add new post.html accordingly"
    (spit (str (fs/path md-dir "empty.md")) "")))

(comment
  (run-tests))