(ns kur.blog.main
  "Entry point fn and the blog server root component"
  (:require
   ;[clojure.core.async :as async]
   [hawk.core :as hawk]
   [kur.blog.monitor :as monitor]))

(defn server
  "Create server with configuration.

   A server is composed of the following components.
   - MONITOR monitors file system changes, and requests WRITER
   - READER is a web server. It reads post and send response.
   - WRITER writes/updates/deletes html posts.
     (note: Currently WRITER doesn't have any state.
            So server doesn't have explicit WRITER component,
            and MONITOR just calls functions of WIRTER.)

   md-dir      An directory to MONITOR post markdown
   html-dir    An directory to WRITE and READ post html
   fs-wait-ms  MONITOR waits fs-wait-ms milliseconds
               after consecutive file events in md-dir and then
               requests WRITER to write/update/delete posts
   port        An port to open to clients"
  [& {:keys [md-dir html-dir fs-wait-ms port] :as config}]
  (assoc config :monitor (monitor/monitor fs-wait-ms md-dir)))

(defn start! [server]
  (-> server
      (update :monitor monitor/start!)))

(defn close! [server]
  (-> server
      (update :monitor monitor/close!)))

(comment
  (server {1 2 3 4})
  (def s (server :md-dir "test/fixture/blog-v1-md"
                 :html-dir "test/fixture/blog-v1-html/"
                 :fs-wait-ms 1000
                 :port 8080))
  (def s (start! s))
  (def s (close! s)))