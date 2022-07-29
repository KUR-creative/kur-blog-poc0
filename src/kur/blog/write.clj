(ns kur.blog.write
  "Write functions"
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [unordered-list link-to]]
            [kur.blog.post :as post]
            [kur.blog.md2x :refer [obsidian-html]]))

;; Entities
(defn post-info [md-path]
  (let [fname (fs/file-name md-path)
        id (first (str/split fname #"\." 3))]
    (post/id-info id)))

;; Templates
(def scale1-viewport
  [:meta {:name "viewport"
          :content "width=device-width, initial-scale=1"}])
(def charset-utf8 [:meta {:charset "utf-8"}])

;; Make html
(defn optional-link
  ([url] (optional-link url url url))
  ([url text] (optional-link url text text))
  ([url text no-link-text] (if url (link-to url text) no-link-text)))

(defn post-html [{:keys [content prev next home]}]
  (html [:head scale1-viewport charset-utf8]
        [:body (list content
                     [:footer [:pre [:br] [:hr]
                               (optional-link prev "prev") "   "
                               (optional-link home "home") "   "
                               (optional-link next "next")]])]))

(defn post-archive-html [post-links]
  (html [:head scale1-viewport charset-utf8]
        [:body (list [:h1 "post archive"]
                     (unordered-list (map optional-link post-links)))]))
                     
;; Actions (has side effects)
(defn write-post [from-md to-html]
  (spit (str to-html)
        (post-html {:content (-> (str from-md) slurp obsidian-html)})))

(comment
  (require '[clojure.spec.alpha :as s]
           '[clojure.spec.gen.alpha :as sg])
  (def md-paths (fs/list-dir "./test/fixture/blog-v1-md"))
  (map post-info md-paths)
  (map post-info (sg/sample (s/gen ::post/id)))

  (def post-links ["http://127.0.0.1:8384/"
                   "https://clojuredocs.org/clojure.core/repeat"
                   "https://clojuredocs.org/clojure.core/cycle"])
  (spit "out/t.html" (post-archive-html post-links))
  (spit "out/t.html"
        (post-html {:content (obsidian-html (slurp "./README.md"))
                    :prev "http://127.0.0.1:8384/"}))

  

  ;; Actions
  #_(def md-fixture-dir)
  (def html-dir "test/fixture/post-html/")
  (def post-md1 "test/fixture/blog-v1-md/kur2004250001.-.오버 띵킹의 함정을 조심하라.md")
  (def post-html1 (fs/path html-dir "오버 띵킹의 함정을 조심하라.html"))

  ; create
  (write-post post-md1 post-html1) ; post/xx-info for state
  (fs/delete post-html1)
  )