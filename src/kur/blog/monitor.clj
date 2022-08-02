(ns kur.blog.monitor
  "Monitor directory and send message to channel"
  (:require [babashka.fs :as fs]
            [clojure.core.async :as async]
            [hawk.core :as hawk]))

(defn dir-watch-spec [paths chan]
  {:paths paths
   :handler (fn [_ e] (async/put! chan e))})

(defn run-monitor [wait-ms chan]
  (async/go-loop [got-event? false]
    (let [t (async/timeout wait-ms)]
      (async/alt!
        chan ([x] (when x ; when chan is closed, loop ends.
                    (println "Read" x "from chan")
                    (recur true)))
        t (do (when got-event?
                (println "Timed out. Act upon events!")
                (println "events are resolved."))
              (recur false))))))

;;
(comment
  (add-tap (bound-fn* prn))
  (def ch (async/chan))

  (def w (hawk/watch! [(dir-watch-spec ["./test/fixture/post-md"] ch)]))
  (hawk/stop! w)

  (run-monitor 4000 ch)
  #_(async/put! ch 2))