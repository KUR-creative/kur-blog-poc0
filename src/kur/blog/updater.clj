(ns kur.blog.updater
  "Updater write/update/delete html post from md using wrtie functions"
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]
            [kur.blog.post :as post]
            [kur.blog.page.post :as page-post]
            [kur.blog.state :as state]))

(defn need-write?
  "Is new-info(a post) need to write to file system?"
  [old-info {id ::post/id pub? ::post/public? :as new-info} id:happened]
  (def old-info old-info)
  (def new-info new-info)
  (def pub? pub?)
  (def id:happened id:happened)
  (def id id)
  (and pub?
       (#{::state/create ::state/update} (id:happened id))
       (s/valid? ::post/id id)
       (post/modified? old-info new-info)))

;;
(defn updater [state in-dirs post-html-dir]
  {::state/state state ::in-dirs in-dirs ::post-html-dir post-html-dir})

(defn update! [updater]
  (let [{state ::state/state
         in-dirs ::in-dirs post-html-dir ::post-html-dir} updater

        old-s @state, new-s (apply post/id:file-info in-dirs)
        happeneds (state/happeneds old-s new-s)

        ids-to-write
        (set (filter #(need-write? (old-s %) (new-s %) happeneds)
                     (keys new-s)))
        assoc-html-path-conditonally
        #(if (and (contains? ids-to-write (::post/id %))
                  (not (contains? % ::post/html-path)))
           (assoc %
                  ::post/html-path
                  (post/html-file-path post-html-dir %))
           %)
        next-s (update-vals (state/next-state old-s new-s happeneds)
                            assoc-html-path-conditonally)]
    (def old-s old-s)
    (def new-s new-s)
    (def next-s next-s)
    (def happeneds happeneds)
    (def ids-to-write ids-to-write)
    (reset! state next-s)

    (run! (fn [{out ::post/html-path inp ::post/md-path}]
            (->> inp slurp page-post/post-html (spit out)))
          (map next-s ids-to-write))))

#_(do
    (def st (atom {"kur2205182112" {} "kur2205182113" {}}))
    (def updater {::state/state st
                  ::in-dirs ["test/fixture/blog-v1-md/"]
                  ::post-html-dir "test/fixture/post-html"}))

;;
(comment
  (def md-dir
    "NOTE: config or policy"
    "test/fixture/blog-v1-md/")

    ;"test/fixture/post-md/"
  )