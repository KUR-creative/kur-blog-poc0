(ns kur.blog.updater
  "Updater write/update/delete html post from md using wrtie functions"
  (:require [babashka.fs :as fs]
            [kur.blog.post :as post]
            [kur.blog.state :as state]))

(defn md-posts [md-dir]
  (let [publics (->> (fs/list-dir md-dir)
                     (map post/file-info) (filter ::post/public?))]
    (zipmap (map ::post/id publics) publics)))



;;;
(defn updater [state in-dirs out-dirs]
  {::state/state state ::in-dirs in-dirs ::out-dirs out-dirs})

#_(def updater {::in-dirs ["test/fixture/blog-v1-md/"]
                ::out-dirs ["test/fixture/post-html"]})
(defn update! [updater]

  #_(let [{in-dirs ::in-dirs out-dirs ::out-dirs} updater
          src-posts (md-posts (first in-dirs))
          src-paths (map #(::post/path (val %)) src-posts)
          dst-paths (->> src-posts
                         (map #(post/parts->fname (val %)))
                         (map #(str (fs/path (first out-dirs) %))))]
      (run! (fn [[src dst]] (fs/copy src dst))
            (map vector src-paths dst-paths))))

;;;
(comment
  (def md-dir
    "NOTE: config or policy"
    "test/fixture/blog-v1-md/")

    ;"test/fixture/post-md/"
  )