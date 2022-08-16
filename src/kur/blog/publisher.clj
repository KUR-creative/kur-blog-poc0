(ns kur.blog.publisher
  (:require [babashka.fs :as fs]
            [clojure.java.io :as io]
            [kur.blog.post :as post]
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

(def id-path
  {"/kur2004250001" "test/fixture/blog-v1-html/kur2004250001.-.오버 띵킹의 함정을 조심하라.html"
   "/kur2205182112" "test/fixture/blog-v1-html/kur2205182112..인간의 우열(편차)보다 사용하는 도구와 환경, 문화의 우열이 퍼포먼스에 더 큰 영향을 미친다.html"
   "/kur2206082055" "test/fixture/blog-v1-html/kur2206082055.Clojure 1.10의 tap은 디버깅 용도(better prn)로 사용할 수 있다.html"
   "/kur2207111708" "test/fixture/blog-v1-html/kur2207111708.Secret Manager 서비스는 어플리케이션의 secret을 안전하게 관리하여.. haha. 하드코딩된 secret을 없애고 주기적으로 자동화된 secret 변경이 가능케 한다.html"
   "/kur2207161305" "test/fixture/blog-v1-html/kur2207161305.+.kill-current-sexp의 Emacs, VSCode 구현.html"})

(defn send-file [req]
  (if-let [page (id-path (:uri req))]
    (resp/file-response page)
    (resp/not-found "ERROR: 404 not found")))

(def app send-file)

;;
(defn publish [state req]
  (tap> (:uri req))
  (if-let [info (@state (subs (:uri req) 1))]
    (resp/file-response (::post/path info))
    (resp/not-found "ERROR: 404 not found")))

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
  (require '[org.httpkit.client :as http]);
  (def s (run-jetty #'app {:port 8080 :join? false}));
  (.stop s);
  (.start s);

  @(http/get "http://localhost:8080/kur2205182112" {:as :text})
  (let [urls ["http://localhost:8080/kur2205182112"
              "http://localhost:8080/kur2206082055"
              "http://localhost:8080/kur2207111708"]
        futures (doall (map #(http/get % {:as :text}) urls))]
    (doseq [resp futures]
      (println (-> @resp :opts :url) " body: " (count (:body @resp)))))

  #_(do ;; create htmls
      (def md-fixture-dir "test/fixture/blog-v1-md")
      (def html-dir "test/fixture/blog-v1-html/")

      (def post-md-paths (fs/list-dir md-fixture-dir))
      (def post-names (map #(-> % fs/strip-ext fs/file-name) post-md-paths))
      (def post-html-paths
        (map #(fs/path html-dir (str % ".html")) post-names))

      (require '[kur.blog.write :refer [write-post]])
      (doseq [[src dst] (map vector post-md-paths post-html-paths)]
        (write-post src dst)))
  (add-tap (bound-fn* prn))
  (def state (atom (post/id:file-info "test/fixture/blog-v1-md"))))

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
  (is (and (not (:running? pub))      (:closed? pub))))