(ns kur.blog.write
  "Writes posts of blog from input md and resource"
  (:require [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sg]
            [clojure.string :as str]
            [com.gfredericks.test.chuck.generators :as ug]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [unordered-list link-to]]
            [kur.util.time :refer [time-format]]
            [kur.util.regex :refer [hangul* alphanumeric*]])
  (:import (com.eclipsesource.v8 NodeJS)))

;; md2x
(defonce nodejs-runtime (NodeJS/createNodeJS))
(def md2x-path "./md2x/out/md2x.js")
(def md2x (.require nodejs-runtime (io/file md2x-path)))

(defn obsidian-html [md]
  (.executeJSFunction md2x "obsidian" (to-array [md])))

;; Rules
(defn digit? [c] (and (>= 0 (compare \0 c)) (>= 0 (compare c \9))))

(s/def :post/author
  (s/and string? #(seq %) #(not (str/includes? % "."))
         #(not (digit? (last %)))))

(def ctime-fmt "YYMMddHHmm")
(def ctime-len (count ctime-fmt)) ; NOTE: it can be different! (eg. "YYY" -> 2022)
(s/def :post/ctime ; md file creation time. 
  ; NOTE: It doesn't check date time validity (eg. 9999999999 is valid)
  (s/with-gen (s/and string? #(re-matches #"\d+" %) #(= (count %) 10))
    #(sg/fmap (fn [inst] (time-format ctime-fmt inst)) (s/gen inst?))))

(defn id-info [post-id]
  (let [author-len (- (count post-id) ctime-len)
        [author ctime] (map #(apply str %) (split-at author-len post-id))]
    {:author (when (s/valid? :post/author author) author)
     :ctime (when (s/valid? :post/ctime ctime) ctime)}))
(s/def :post/id
  (s/with-gen (s/and string? #(every? some? (vals (id-info %))))
    #(sg/fmap (fn [[author ctime]] (str author ctime))
              (sg/tuple (s/gen :post/author) (s/gen :post/ctime)))))

(s/def :post/meta-str ; + means public, else private.
  #{"+" "-" ""})

(def obsidian-title-symbol* #"[\!\,\ \.\+\=\-\_\(\)]*")
(def gen-post-title
  "<id>[.<meta>].<title>.md  NOTE: title can be empty string"
  (sg/fmap (fn [[syms alphanums hanguls]]
            (->> (str syms alphanums hanguls) vec shuffle (apply str)))
          (sg/tuple (ug/string-from-regex obsidian-title-symbol*)
                    (ug/string-from-regex alphanumeric*)
                    (ug/string-from-regex hangul*))))
(s/def :post/title
  (s/with-gen string? (fn [] gen-post-title)))

;; Entities
(defn post-info [md-path]
  (let [fname (fs/file-name md-path)
        id (first (str/split fname #"\." 3))]
    (id-info id)))

;; Templates
(def scale1-viewport
  [:meta {:name "viewport"
          :content "width=device-width, initial-scale=1"}])
          
;; Make html
(defn optional-link
  ([url] (optional-link url url url))
  ([url text] (optional-link url text text))
  ([url text no-link-text] (if url (link-to url text) no-link-text)))

(defn post-html [{:keys [content prev next home]}]
  (html [:head scale1-viewport]
        [:body (list content
                     [:footer [:pre [:br] [:hr]
                               (optional-link prev "prev") "   "
                               (optional-link home "home") "   "
                               (optional-link next "next")]])]))

(defn post-archive-html [post-links]
  (html [:head scale1-viewport]
        [:body (list [:h1 "post archive"]
                     (unordered-list (map optional-link post-links)))]))

(comment
  (def md-paths (fs/list-dir "./test/fixture/blog-v1-md"))
  (id-info "asd1234567890")
  (map post-info md-paths)
  (map post-info (sg/sample (s/gen :post/id)))

  (def post-links ["http://127.0.0.1:8384/"
                   "https://clojuredocs.org/clojure.core/repeat"
                   "https://clojuredocs.org/clojure.core/cycle"])
  (spit "out/t.html" (post-archive-html post-links))
  (spit "out/t.html"
        (post-html {:content (obsidian-html (slurp "./README.md"))
                    :prev "http://127.0.0.1:8384/"}))

  #_((obsidian-html "### 3")
     (obsidian-html (slurp "./README.md"))
     (spit "out/t.html" (obsidian-html (slurp "./README.md"))))

  (str/join " " (->> (fs/list-dir "/home/dev/outer-brain/thinks/")
                     (map fs/file-name) (map set)
                     (apply clojure.set/union) (sort)))

  (s/exercise :post/ctime 20)
  (s/exercise :post/author 20)
  (s/exercise :post/id 20)
  (s/exercise :post/meta-str)

  (sg/sample (ug/string-from-regex obsidian-title-symbol*) 30)
  (sg/sample gen-post-title 30)
  (s/exercise :post/title)
  )  