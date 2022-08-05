(ns kur.blog.updater
  "Updater write/update/delete html post from md using wrtie functions"
  (:require [babashka.fs :as fs]
            [kur.blog.post :as post]
            [kur.blog.state :as state]))

;;;
(defn updater [state in-dirs out-dirs]
  {::state/state state ::in-dirs in-dirs ::out-dirs out-dirs})

(defn update! [updater]
  (let [{state ::state/state
         in-dirs ::in-dirs out-dirs ::out-dirs} updater
        old @state, new (apply post/id:file-info in-dirs)
        happeneds (state/happeneds old new)]
    (reset! state (state/next-state old new happeneds))))
#_(def updater {::state/state (atom {"kur2205182112" {}
                                     "kur2205182113" {}})
                ::in-dirs ["test/fixture/blog-v1-md/"]
                ::out-dirs ["test/fixture/post-html"]})

;;;
(comment
  (def md-dir
    "NOTE: config or policy"
    "test/fixture/blog-v1-md/")

    ;"test/fixture/post-md/"
  )