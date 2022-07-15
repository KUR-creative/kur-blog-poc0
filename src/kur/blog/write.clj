(ns kur.blog.write
  "Writes posts of blog from input md and resource"
  (:require [clojure.java.io :as io]
            [hiccup.core :refer [html]]
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
(defn page [head-xs body-xs] ;; TODO? [[xs] [ys]]
  (list (doctype :html5) [:head head-xs] [:body body-xs]))

;;
(defn post [{:keys [content prev-link next-link archive-link]}]
  (let [template (html (page (list scale1-viewport) (list "%s")))]
    (format template content)))

(comment
  (spit "out/t.html"
        (post {:content (obsidian-html (slurp "./README.md"))}))

  #_((obsidian-html "### 3")
     (obsidian-html (slurp "./README.md"))
     (spit "out/t.html" (obsidian-html (slurp "./README.md"))))
  )