(ns kur.blog.page.template)

(def scale1-viewport
  [:meta {:name "viewport"
          :content "width=device-width, initial-scale=1"}])
(def charset-utf8 [:meta {:charset "utf-8"}])

(def basic-head [scale1-viewport charset-utf8])