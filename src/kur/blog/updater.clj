(ns kur.blog.updater
  "Updater write/update/delete html post from md using wrtie functions"
  (:require [clojure.spec.alpha :as s]
            [kur.blog.post :as post]
            [kur.blog.state :as state]))

;; write only: (and post? public? mtime-changed? file-changed?)
(defn need-write?
  "Is new-info(a post) need to write to file system?"
  [old-info {id ::post/id pub? ::post/public? :as new-info} id:happened]
  (and pub?
       (#{::state/create ::state/update} (id:happened id))
       (s/valid? ::post/id id)
       (post/modified? old-info new-info)))

;;
(defn updater [state in-dirs out-dirs]
  {::state/state state ::in-dirs in-dirs ::out-dirs out-dirs})

(defn update! [updater]
  (let [{state ::state/state
         in-dirs ::in-dirs out-dirs ::out-dirs} updater
        old-s @state, new-s (apply post/id:file-info in-dirs)
        happeneds (state/happeneds old-s new-s)
        ids-to-write (filter #(need-write? (old-s %) (new-s %) happeneds)
                             (keys new-s))]
    (reset! state (state/next-state old-s new-s happeneds))
    #_ids-to-write))
#_(def updater {::state/state (atom {"kur2205182112" {}
                                     "kur2205182113" {}})
                ::in-dirs ["test/fixture/blog-v1-md/"]
                ::out-dirs ["test/fixture/post-html"]})

;;
(comment
  (def md-dir
    "NOTE: config or policy"
    "test/fixture/blog-v1-md/")

    ;"test/fixture/post-md/"
  )