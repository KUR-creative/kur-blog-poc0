(ns kur.blog.page.post
  (:require
   [hiccup.page :refer [html5 include-css include-js]]
   [kur.blog.md2x :refer [obsidian-html]]
   [kur.blog.page.template :refer [scale1-viewport charset-utf8]]))

(defn post-html [text & {:keys [js-paths css-paths]}]
  (html5 [:head scale1-viewport charset-utf8
          (apply include-css css-paths)
          (apply include-js js-paths)]
         [:body (obsidian-html text)]))

;;
(comment
  (def text "# 1 \n ## 2 \n ppap \n\n bbab \n - 1 \n - 22")
  (spit "out/t.html" (post-html text {:css-paths []}))
  (spit "out/t.html"
        (post-html text {:css-paths ["test/fixture/css/test-p/red-p.css"]})))
