(ns kur.blog.updater
  "Updater write/update/delete html post from md using wrtie functions"
  (:require [babashka.fs :as fs]
            [kur.blog.post :as post]))

(defn md-posts [md-dir]
  (let [publics (->> (fs/list-dir md-dir)
                     (map post/file-info) (filter ::post/public?))]
    (zipmap (map ::post/id publics) publics)))

(defn happeneds [old-m new-m]
  (let [all-ks (apply conj (set (keys old-m)) (keys new-m))]
    (zipmap all-ks
            (map post/happened (map old-m all-ks) (map new-m all-ks)))))

;;;
(defn updater [in-dirs out-dirs]
  {::in-dirs in-dirs
   ::out-dirs out-dirs})

(defn update! [updater]
  (prn 'update!))

;;;
(comment
  (def md-dir
    "NOTE: config or policy"
    "test/fixture/blog-v1-md/")

    ;"test/fixture/post-md/"

  (def old-m {:0 0 :1 1 :2 2 :3 3 :4 4})
  (def new-m                {:3 3 :4 44 :5 5 :6 6 :7 7})
  (happeneds old-m new-m))