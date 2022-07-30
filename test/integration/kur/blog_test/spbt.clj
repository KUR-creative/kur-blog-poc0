(ns kur.blog-test.spbt
  "Blog Integration Test Using Stateful PBT"
  (:require
   [babashka.fs :as fs]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [clojure.test.check.generators :as g]
   [kur.blog.post :as post]
   [kur.blog.reader :refer [url-path-set]]
   [kur.util.generator :refer [string-from-regexes]]
   [kur.util.regex :refer [ascii* common-whitespace* hangul*]]))

;;; Generators and Specs
(s/def ::md-text
  (s/with-gen string?
    #(string-from-regexes ascii* common-whitespace* hangul*)))

(s/def ::post (s/keys :req [::post/public? ::post/title ::md-text]))
(s/def ::state (s/map-of ::post/id ::post))

(defn url [scheme ip port path]
  (str scheme "://" ip ":" port "/" path))

(defn gen-valid-url [state]
  (g/fmap #(url "http" "localhost" 8080 %)
          (g/elements (url-path-set state))))
(defn gen-invalid-url [state]
  (->> (s/gen (s/or :i+t (s/cat :i ::post/id :t (s/? ::post/title))
                    :s (s/tuple (s/and string? seq))))
       (g/fmap #(str/join "." %))
       (g/such-that #(not (contains? (url-path-set state) %)))
       (g/fmap #(url "http" "localhost" 8080 %))))

;;; Operations
(defn gen-create [state]
  (g/fmap #(hash-map :op :create, :id (key %), :post (val %))
          (g/elements state)))

(defn gen-read [state]
  (g/let [[url valid?]
          (g/one-of [(g/tuple (gen-valid-url state) (g/return true))
                     (g/tuple (gen-invalid-url state) (g/return false))])]
    (if valid?
      (let [id (::post/id (-> url fs/file-name post/fname->parts))]
        {:op :read, :url url, :id id, :post (state id)})
      {:op :read, :url url})))

(defn gen-delete [state]
  (g/fmap #(hash-map :op :delete :id (key %)) (g/elements state)))

(defn gen-ops [state]
  (g/vector (g/one-of [(gen-create state) (gen-read state)
                       (gen-delete state)])))

;;;
(comment
  #_(require '[babashka.fs :as fs])
  #_(str/join " " ;; To know used characters
              (->> (fs/list-dir "/home/dev/outer-brain/thinks/")
                   (map #(-> % str slurp set))
                   (apply clojure.set/union) (sort)))

  (def state (last (g/sample (s/gen ::state))))

  ;;; gens & specs
  (g/sample (s/gen ::md-text) 30)
  (g/sample (s/gen ::post) 30)
  (g/sample (s/gen ::state) 10)

  (url "http" "localhost" 8384 "")
  (g/sample (gen-valid-url state) 20)
  (g/sample (gen-invalid-url state) 20)

  ;;; Operations
  (url-path-set state) ;; NOTE: unit test for reader/url-path-set

  (g/sample (gen-create state) 10)
  (g/sample (gen-read state) 20)
  (g/sample (gen-delete state) 10)

  (g/sample (gen-ops state) 20)

  ;;; Runners
  )
