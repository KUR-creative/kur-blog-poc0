(ns kur.blog.page.post
  (:require
   [hiccup.core :refer [html]]
   [kur.blog.md2x :refer [obsidian-html]]
   [kur.blog.page.template :refer [basic-head]]))

(defn post-html [text]
  (html (vec (cons :head basic-head))
        [:body (obsidian-html text)]))

;;
(comment
  (def text "# 1 \n ## 2 \n ppap \n\n bbab \n - 1 \n - 22")
  (spit "out/t.html" (post-html text)))
