(ns kur.blog.post
  "Blog post entity"
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sg]
            [clojure.string :as str]
            [com.gfredericks.test.chuck.generators :as ug]
            [kur.util.time :refer [time-format]]
            [kur.util.regex :refer [hangul* alphanumeric*]]
            [medley.core :refer [assoc-some]]))

(defn digit? [c] ; TODO: move to util?
  (and (>= 0 (compare \0 c)) (>= 0 (compare c \9))))
(s/def ::author
  (s/and string? #(seq %) #(not (str/includes? % "."))
         #(not (digit? (last %)))))

(def ctime-fmt "YYMMddHHmm")
(def ctime-len (count ctime-fmt)) ; NOTE: it can be different! (eg. "YYY" -> 2022)
(s/def ::ctime ; md file creation time. 
  ; NOTE: It doesn't check date time validity (eg. 9999999999 is valid)
  (s/with-gen (s/and string? #(re-matches #"\d+" %) #(= (count %) 10))
    #(sg/fmap (fn [inst] (time-format ctime-fmt inst)) (s/gen inst?))))

(defn id-info [post-id]
  (let [author-len (- (count post-id) ctime-len)
        [author ctime] (map #(apply str %) (split-at author-len post-id))]
    {:author (when (s/valid? ::author author) author)
     :ctime (when (s/valid? ::ctime ctime) ctime)}))
(s/def ::id
  (s/with-gen (s/and string? #(every? some? (vals (id-info %))))
    #(sg/fmap (fn [[author ctime]] (str author ctime))
              (sg/tuple (s/gen ::author) (s/gen ::ctime)))))

(s/def ::meta-str ; + means public, else private.
  #{"+" "-"})

(def obsidian-title-symbol* #"[\!\,\ \.\+\=\-\_\(\)]*")
(def gen-post-title
  "<id>[.<meta>].<title>.md  NOTE: title can be empty string"
  (sg/fmap (fn [[syms alphanums hanguls]]
             (->> (str syms alphanums hanguls) vec shuffle (apply str)))
           (sg/tuple (ug/string-from-regex obsidian-title-symbol*)
                     (ug/string-from-regex alphanumeric*)
                     (ug/string-from-regex hangul*))))
(s/def ::title
  (s/with-gen (s/and string?
                     #(not (s/valid? ::meta-str
                                     (first (str/split % #"\." 2)))))
    (fn [] gen-post-title)))

(s/def ::file-name-parts
  (s/keys :req [::id] :opt [::meta-str ::title]))

(def post-extension "md")

(defn parts->fname [fname-parts]
  (str (->> fname-parts
            ((juxt ::id ::meta-str ::title))
            (remove nil?)
            (str/join ".")) "." post-extension))
            
(defn fname->parts [post-fname]
  (let [base-name (fs/strip-ext post-fname)
        [id meta title] (str/split base-name #"\." 3)
        ret {::id id}]
    (if (s/valid? ::meta-str meta)
      (assoc-some ret ::meta-str meta ::title title)
      (if (s/valid? ::title meta)
        (assoc ret ::title (if title (str meta "." title) meta))
        ret))))

(s/def ::file-name
  (s/with-gen string?
    #(sg/fmap parts->fname (s/gen ::file-name-parts))))

(defn file-name-info [post-fname]
  (let [parts (str/split post-fname #"\.")]
    parts))

(comment
  (id-info "asd1234567890")
  (s/exercise ::ctime 20)
  (s/exercise ::author 20)
  (s/exercise ::id 20)
  (s/exercise ::meta-str)

  (sg/sample (ug/string-from-regex obsidian-title-symbol*) 30)
  (sg/sample gen-post-title 30)
  (s/exercise ::title)
  (s/explain ::title "+.asdf")
  (s/explain ::title "-.asdf")
  (s/explain ::title ".asdf")

  (s/exercise ::file-name-parts 20)
  (sg/sample (s/gen ::file-name-parts) 20)
  (sg/sample (s/gen ::file-name) 20)
  (map #(vector % (file-name-info %))
       (sg/sample (s/gen ::file-name) 20))
  (s/explain ::file-name "kur1234567890.md")

  (require '[clojure.test :refer [is]])
  (doseq [parts (reverse (sg/sample (s/gen ::file-name-parts) 1000))]
    (is (= parts (fname->parts (parts->fname parts)))
        (str "\"" (parts->fname parts) "\"")))

  #_(str/join " " ;; To know used characters
              (->> (fs/list-dir "/home/dev/outer-brain/thinks/")
                   (map fs/file-name) (map set)
                   (apply clojure.set/union) (sort))))