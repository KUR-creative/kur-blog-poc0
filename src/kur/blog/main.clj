(ns kur.blog.main
  "Entry point fn and the blog server root component"
  (:require
   ;[clojure.core.async :as async]
   [hawk.core :as hawk]
   [kur.blog.monitor :as monitor]
   [kur.blog.updater :as updater]
   [kur.blog.post :as post]
   [kur.blog.publisher :as publisher]))

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
  (let [state (atom {})
        updater (updater/updater state [md-dir] [html-dir])
        monitor (monitor/monitor fs-wait-ms
                                 #(updater/update! updater)
                                 md-dir)
        publisher (publisher/publisher state {:port port})]
    (assoc config
           :state state :updater updater
           :monitor monitor :publisher publisher)))

(defn start! [server]
  #_(println "Start server!")
  (-> server
      (update :monitor monitor/start!)
      (update :publisher publisher/start!)))

#_(require '[babashka.fs :as fs])
(defn num-public-posts [server]
  (count (filter #(::post/public? (val %)) @(:state server)))
  #_(def server server)
  #_(->> (:html-dir server)
         fs/list-dir (filter #(re-find #"\.md$" (str %)))
         count))

(defn close! [server]
  #_(println "Close server!")
  (-> server
      (update :monitor monitor/close!)
      (update :publisher publisher/close!)))

;;;
(comment
  (server {1 2 3 4})
  (def s (server :md-dir "test/fixture/blog-v1-md"
                 :html-dir "test/fixture/blog-v1-html/"
                 :fs-wait-ms 1000
                 :port 8080))
  (def s (start! s))
  (def s (close! s)))

(do (require '[clojure.test :refer [run-all-tests]])
    (println '----------------------------------------------------)
    #_(run-all-tests #"kur\.blog(.*-test|-test.*)") ;; mine all
    (run-all-tests #"kur\..*-test")) ;; only units