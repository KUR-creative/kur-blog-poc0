(ns kur.blog.monitor
  "Monitor directory and send message to channel"
  (:require [babashka.fs :as fs]
            [clojure.core.async :as async]
            [hawk.core :as hawk]
            [kur.blog.monitor :as monitor]))

(defn watch-spec [paths chan]
  {:paths paths
   :handler (fn [_ e] (async/put! chan e))})

(defn loop! [request-fn wait-ms chan] ;; TODO: timeout-ms event-chan
  (async/go-loop [got-event? false]
    (let [t (async/timeout wait-ms)]
      (async/alt!
        chan ([x] (when x ; when chan is closed, loop ends.
                    #_(println "Read" x "from chan")
                    (recur true)))
        t (do (when got-event?
                #_(println "Timed out. Act upon events!")
                (request-fn)
                #_(println "events are resolved."))
              (recur false))))))

;;
(defn monitor
  "Return monitor entity from config"
  [wait-ms request-fn monitor-dir & more-dirs]
  (let [ch (async/chan)]
    {::event-chan ch
     ::watch-spec (watch-spec (cons monitor-dir more-dirs) ch)
     ::wait-ms wait-ms
     ::request-fn request-fn
     ::running? false
     ::closed? false}))

(defn start! ;; TODO: destructuring
  "Start monitor and return started monitor. Idempotent fn."
  [monitor]
  (when-not (::closed? monitor)
    (if-not (::running? monitor)
      (let [go-loop (loop! (::request-fn monitor)
                           (::wait-ms monitor)
                           (::event-chan monitor))
            watch (hawk/watch! [(::watch-spec monitor)])]
        (assoc monitor ::watch watch ::go-loop go-loop ::running? true))
      monitor)))

(defn close!
  "Close monitor and return closed monitor. Closed one can't run again."
  [monitor]
  (when-not (::closed? monitor)
    (if (::running? monitor)
      (do (async/close! (::go-loop monitor))
          (async/close! (::event-chan monitor))
          (hawk/stop! (::watch monitor))
          (assoc monitor ::running? false ::closed? true))
      monitor)))

;;
(comment
  (add-tap (bound-fn* prn))
  (def ch (async/chan))

  (def w (hawk/watch! [(watch-spec ["./test/fixture/post-md"] ch)]))
  (hawk/stop! w)

  #_(run-monitor 4000 ch)
  #_(async/put! ch 2)

  (monitor 10 #(prn "no-op") "test/fixture/blog-v1-md")
  (monitor 100 #(prn "no-op")
           "test/fixture/blog-v1-md" "test/fixture/blog-v1-html"))

(comment (require '[clojure.test :refer [is]])
         (def m (monitor 1500 #(prn "no-op") "test/fixture/blog-v1-md"))
         (is (and (not (::running? m)) (not (::closed? m))))
         (def m (start! m))
         (is (and      (::running? m)  (not (::closed? m))))
         (def m (start! m))
         (is (and      (::running? m)  (not (::closed? m))))
         (def m (close! m))
         (is (and (not (::running? m))      (::closed? m))))