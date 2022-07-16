(ns kur.blog.write
  "Writes posts of blog from input md and resource"
  (:require [clojure.java.io :as io]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [unordered-list link-to]]
            [hiccup.page :refer [doctype]])
  (:import (com.eclipsesource.v8 NodeJS)))

;;
(defonce nodejs-runtime (NodeJS/createNodeJS))
(def md2x-path "./md2x/out/md2x.js")
(def md2x (.require nodejs-runtime (io/file md2x-path)))

(defn obsidian-html [md]
  (.executeJSFunction md2x "obsidian" (to-array [md])))

;;
(def scale1-viewport
  [:meta {:name "viewport"
          :content "width=device-width, initial-scale=1"}])

;;
(defn post [{:keys [content prev next home]}]
  (let [optional-link (fn [url text] (if url (link-to url text) text))
        footer [:footer [:pre [:br] [:hr]
                         (optional-link prev "prev") "   "
                         (optional-link home "home") "   "
                         (optional-link next "next")]]]
    (html [:head scale1-viewport] [:body (list content footer)])))
    
(defn post-archive [post-links]
  (html [:head scale1-viewport]
        [:body (list [:h1 "post archive"]
                     (unordered-list (map link-to post-links post-links)))]))

(comment
  (def post-links ["http://127.0.0.1:8384/"
                   "https://clojuredocs.org/clojure.core/repeat"
                   "https://clojuredocs.org/clojure.core/cycle"])
  (spit "out/t.html" (post-archive post-links))
  (spit "out/t.html" (post {:content (obsidian-html (slurp "./README.md"))
                            :prev "http://127.0.0.1:8384/"}))

  #_((obsidian-html "### 3")
     (obsidian-html (slurp "./README.md"))
     (spit "out/t.html" (obsidian-html (slurp "./README.md")))))