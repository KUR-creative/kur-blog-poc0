(ns kur.blog.publisher
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]
            [kur.blog.post :as post]
            [kur.blog.resource :as resource]
            [kur.blog.state :as state]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as resp]))

(defn url-path-set
  "Allowed url path: 'id' or 'id.title'.
   NOTE: 'resource.path' is not yet implemented.
   state is {\"id\" post/file-info}"
  [state]
  (let [ids (-> state keys vec)
        titles (map #(-> % state ::post/title) ids)]
    (into (set ids) (map #(str %1 "." %2) ids titles))))

;;
(def not-found-body "ERROR: 404 not found")

(defn entity-id
  "post-id or resource-id"
  [uri]
  (let [id-str (subs uri 1)]
    (if-let [post-id (::post/id (post/fname->parts id-str))]
      post-id
      id-str)))

(defn publish [state req]
  (tap> (:uri req))
  (def state state) (def req req) (def uri (:uri req))
  (let [info (@state (entity-id (:uri req)))]
    (def info info)
    ;; TODO: Could be refactored using polymorphism
    ;; if entity types are more than 2, use polymorphism!
    (if (:public? info)
      (if (::post/id info) ; if post
        (if-let [html-path (post/cached-html-path info)] ; cache hit
          (resp/file-response html-path)
          (resp/not-found not-found-body)) ; TODO? do when cache miss?
        (if (fs/exists? (::resource/id info))
          (resp/file-response (::resource/id info))
          (resp/not-found not-found-body)))
      (resp/not-found not-found-body))))

;;
(defn publisher [state jetty-opts] ;; TODO: set ssl, later..
  (merge {::state/state state
          :running? false :closed? false} ; State machine
         (cond-> jetty-opts ; jetty-opts (my) defaults
           (nil? (:join? jetty-opts)) (assoc :join? false))))

(defn start! [pub] ; TODO: Refactor common state machine(monitor)
  (when-not (:closed? pub)
    (if-not (:running? pub)
      (let [jetty-keys [:port :join?]]
        (def handler #(publish (::state/state pub) %))
        (assoc pub ; TODO? pub also include handler? or not?
               ::server (run-jetty #'handler (select-keys pub jetty-keys))
               :running? true))
      pub)))

(defn close! [pub]
  (when-not (:closed? pub)
    (if (:running? pub)
      (do (.stop (::server pub))
          (assoc pub :running? false :closed? true))
      pub)))

(comment
;(do
  #_(do ;; create htmls
      (def md-fixture-dir "test/fixture/blog-v1-md")
      (def html-dir "test/fixture/blog-v1-html/")

      (def post-md-paths (fs/list-dir md-fixture-dir))
      (def post-names (map #(-> % fs/strip-ext fs/file-name) post-md-paths))
      (def post-html-paths
        (map #(fs/path html-dir (str % ".html")) post-names))


      (doseq [[src dst] (map vector post-md-paths post-html-paths)]
        (write-post src dst)))
  (def state (atom (post/id:file-info "test/fixture/blog-v1-md")))

  (require '[org.httpkit.client :as http]);
  @(http/get "http://localhost:8080/kur2205182112" {:as :text}))

(comment ;do ;; TODO: Refactor common state machine(monitor) with test
  (require '[clojure.test :refer [is]])
  (def pub (publisher state {:port 8080}))
  (is (and (not (:running? pub)) (not (:closed? pub))))

  (def pub (start! pub))
  (is (and      (:running? pub)  (not (:closed? pub))))
  (def old-pub pub)

  (def pub (start! pub))
  (is (and      (:running? pub)  (not (:closed? pub))))
  (def idempotent-pub pub)
  (is (= old-pub idempotent-pub))

  (def pub (close! pub))
  (is (and (not (:running? pub))      (:closed? pub)))

  (add-tap (bound-fn* prn)))