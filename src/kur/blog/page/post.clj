(ns kur.blog.page.post
  (:require
   [hiccup.page :refer [html5]]
   [kur.blog.md2x :refer [obsidian-html]]
   [kur.blog.page.template :refer [head]]))

(defn post-html [css-paths md-text]
  (html5 (head :css-paths css-paths)
         [:body (obsidian-html md-text)]))

;;
(comment
  (def text "# 1 \n ## 2 \n ppap \n\n bbab \n - 1 \n - 22")
  (spit "out/t.html" (post-html [] text))
  (spit "out/t.html"
        (post-html ["test/fixture/css/test-p/red-p.css"] text)))
