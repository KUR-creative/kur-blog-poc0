(ns kur.blog.main
  "Entry point fn and the blog server root component"
  (:require
   ;[clojure.core.async :as async]
   [hawk.core :as hawk]
   [kur.blog.monitor :as monitor]
   [kur.blog.updater :as updater]))

(defn server
  "Create server with configuration.

   A server is composed of the following components.
   - MONITOR monitors file system changes, and requests UPDATER
   - PUBLISHER is a web server. It reads post and send response.
   - UPDATER writes/updates/deletes html posts.

   md-dir      An directory to MONITOR post markdown
   html-dir    An directory to UPDATE and PUBLISH post html
   fs-wait-ms  MONITOR waits fs-wait-ms milliseconds
               after consecutive file events in md-dir and then
               requests UPDATER to write/update/delete posts
   port        An port to open to clients"
  [& {:keys [md-dir html-dir fs-wait-ms port] :as config}]
  (let [updater (updater/updater [md-dir] [html-dir])
        monitor (monitor/monitor fs-wait-ms
                                 #(updater/update! updater)
                                 md-dir)]
    (assoc config :updater updater :monitor monitor)))

(defn start! [server]
  (println "Start server!")
  (-> server
      (update :monitor monitor/start!)))

(defn num-public-posts [server]
  0)

(defn close! [server]
  (println "Close server!")
  (-> server
      (update :monitor monitor/close!)))

;;;
(comment
  (server {1 2 3 4})
  (def s (server :md-dir "test/fixture/blog-v1-md"
                 :html-dir "test/fixture/blog-v1-html/"
                 :fs-wait-ms 1000
                 :port 8080))
  (def s (start! s))
  (def s (close! s)))