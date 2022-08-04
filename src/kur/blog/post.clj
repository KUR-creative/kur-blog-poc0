(ns kur.blog.post
  "Blog post entity"
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sg]
            [clojure.string :as str]
            [kur.util.generator :refer [string-from-regexes]]
            [kur.util.regex :refer [hangul* alphanumeric*]]
            [kur.util.string :refer [digit?]]
            [kur.util.time :refer [time-format]]
            [medley.core :refer [assoc-some]]))

;;; Post id parts
(s/def ::author
  (s/and string? #(seq %) #(not (str/includes? % "."))
         #(not (digit? (last %)))))

(def create-time-fmt "YYMMddHHmm")
(def create-time-len (count create-time-fmt)) ; NOTE: it can be different! (eg. "YYY" -> 2022)
(s/def ::create-time ; md file creation time. 
  ; NOTE: It doesn't check date time validity (eg. 9999999999 is valid)
  (s/with-gen (s/and string? #(re-matches #"\d+" %) #(= (count %) 10))
    #(sg/fmap (fn [inst] (time-format create-time-fmt inst))
              (s/gen inst?))))

;;; Post file name parts
(defn id-info [post-id]
  (let [author-len (- (count post-id) create-time-len)
        [author create-time]
        (map #(apply str %) (split-at author-len post-id))]
    {:author (when (s/valid? ::author author) author)
     :create-time (when (s/valid? ::create-time create-time) create-time)}))
(s/def ::id
  (s/with-gen (s/and string? #(every? some? (vals (id-info %))))
    #(sg/fmap (fn [[author create-time]] (str author create-time))
              (sg/tuple (s/gen ::author) (s/gen ::create-time)))))

(s/def ::meta-str ; + means public, else private.
  #{"+" "-"})

(def obsidian-title-symbol* #"[\!\,\ \.\+\=\-\_\(\)]*")
(def gen-post-title
  "<id>[.<meta>].<title>.md  NOTE: title can be empty string"
  (string-from-regexes obsidian-title-symbol* alphanumeric* hangul*))

(s/def ::title
  (s/with-gen (s/and string?
                     #(not (s/valid? ::meta-str
                                     (first (str/split % #"\." 2)))))
    (fn [] gen-post-title)))

;;; Post file name <-> parts round trip
(s/def ::file-name-parts
  (s/keys :req [::id] :opt [::meta-str ::title]))

(def post-extension "md")

(defn parts->fname
  "post-fname is (fs/file-name path). post-fname includes .extension."
  [fname-parts]
  (str (->> fname-parts
            ((juxt ::id ::meta-str ::title))
            (remove nil?)
            (str/join ".")) "." post-extension))

(defn fname->parts
  "post-fname is (fs/file-name path). post-fname includes .extension."
  [post-fname]
  (let [base-name (fs/strip-ext post-fname)
        [id meta title] (str/split base-name #"\." 3)
        ret {::id id}]
    (if (s/valid? ::meta-str meta)
      (assoc-some ret ::meta-str meta ::title title)
      (if (s/valid? ::title meta)
        (assoc ret ::title (if title (str meta "." title) meta))
        ret))))

(s/def ::file-name ;; file name contains extension.
  (s/with-gen (s/and string? #(re-find #"\.md$" %))
    #(sg/fmap parts->fname (s/gen ::file-name-parts))))

;;; Post information
(s/def ::public? boolean?)

(def public?
  "meta-str to public? policy"
  {"+" true})

(defn file-info [path]
  (if (and (fs/exists? path)
           (s/valid? ::file-name (str path))) ; Check stricter? p in md dir?
    (let [info (-> path fs/file-name fname->parts)]
      (assoc info
             ::public? (public? (::meta-str info))
             ::last-modified-time (fs/last-modified-time path)))
    {}))

(defn happened [old-info new-info]
  {:pre [(or old-info new-info)]}
  (cond (nil? old-info)       ::create
        (nil? new-info)       ::delete
        (= old-info new-info) ::as-is
        :else                 ::update))

;;
(comment
  (id-info "asd1234567890")
  (s/exercise ::create-time 20)
  (s/exercise ::author 20)
  (s/exercise ::id 20)
  (s/exercise ::meta-str)

  (require '[com.gfredericks.test.chuck.generators :as g'])
  (sg/sample (g'/string-from-regex obsidian-title-symbol*) 30)
  (sg/sample gen-post-title 30)
  (s/exercise ::title)
  (s/explain ::title "+.asdf")
  (s/explain ::title "-.asdf")
  (s/explain ::title ".asdf")

  (s/exercise ::file-name-parts 20)
  (sg/sample (s/gen ::file-name-parts) 20)
  (sg/sample (s/gen ::file-name) 20)
  (s/explain ::file-name "kur1234567890.md")
  (s/explain ::file-name "kur1234567890")

  (mapv file-info (sg/sample (s/gen ::file-name) 20))
  #_(def path "test/fixture/blog-v1-md/kur2004250001.-.오버 띵킹의 함정을 조심하라.md")

  (def info {:id "k1234567890"})

  (require '[clojure.test :refer [is]])
  (is (thrown? AssertionError (happened nil nil)))
  [(happened nil info) ;; create
   (happened info nil) ;; delete
   (happened info info) ;; as is
   (happened info (assoc info ::public? true)) ;; update
   ])

(do (require '[clojure.test :refer [run-all-tests]])
    (println '----------------------------------------------------)
    #_(run-all-tests #"kur\.blog(.*-test|-test.*)") ;; mine all
    (run-all-tests #"kur\..*-test")) ;; only units

#_(str/join " " ;; To know used characters
            (->> (fs/list-dir "/home/dev/outer-brain/thinks/")
                 (map fs/file-name) (map set)
                 (apply clojure.set/union) (sort)))