(ns kur.blog-test.spbt
  "Blog Integration Test Using Stateful PBT"
  (:require
   [babashka.fs :as fs]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.generators :as g]
   [clojure.test.check.properties :refer [for-all] :rename {for-all defp}]
   [kur.blog.post :as post]
   [kur.blog.reader :refer [url-path-set]]
   [kur.util.generator :refer [string-from-regexes]]
   [kur.util.regex :refer [ascii* common-whitespace* hangul*]]))

;;; Generators and Specs
(s/def ::md-text
  (s/with-gen string?
    #(string-from-regexes ascii* common-whitespace* hangul*)))

(s/def ::post (s/keys :req [::post/public? ::post/title ::md-text]))
(s/def ::id:post (s/map-of ::post/id ::post :min-count 1))

(defn url [scheme ip port path]
  (str scheme "://" ip ":" port "/" path))

(defn gen-valid-url [id:post]
  (g/fmap #(url "http" "localhost" 8080 %)
          (g/elements (url-path-set id:post))))
(defn gen-invalid-url [id:post]
  (->> (s/gen (s/or :i+t (s/cat :i ::post/id :t (s/? ::post/title))
                    :s (s/tuple (s/and string? seq))))
       (g/fmap #(str/join "." %))
       (g/such-that #(not (contains? (url-path-set id:post) %)))
       (g/fmap #(url "http" "localhost" 8080 %))))

;;; Operations
(defn gen-create [id:post]
  (g/fmap #(hash-map :kind :create, :id (key %), :post (val %))
          (g/elements id:post)))

(defn gen-read [id:post]
  (g/let [[url valid?]
          (g/one-of [(g/tuple (gen-valid-url id:post) (g/return true))
                     (g/tuple (gen-invalid-url id:post) (g/return false))])]
    (if valid?
      (let [id (::post/id (-> url fs/file-name post/fname->parts))]
        {:kind :read, :url url, :id id, :post (id:post id)})
      {:kind :read, :url url})))

(defn gen-delete [id:post]
  (g/fmap #(hash-map :kind :delete :id (key %)) (g/elements id:post)))

(defn gen-ops [id:post]
  (g/vector (g/one-of [(gen-create id:post)
                       #_(gen-read id:post)
                       (gen-delete id:post)])))

;;; Runners
(defn run-model [state op]
  (let [next-state (case (:kind op)
                     :create (assoc state (:id op) (:post op))
                     :delete (dissoc state (:id op)))]
    {:next-state next-state
     :expect (if (#{:create :delete} (:kind op)) ;; ret = #public-posts
               (->> (vals next-state)
                    (filter ::post/public?) count)
               (throw (Exception. "read result (not implemented)")))}))

(defn run-actual [op]
  0)

;;; Tests
(defspec model-test 100
  (defp [operations (g/bind (s/gen ::id:post) gen-ops)]
    (loop [state {}, ops operations]
      (if-let [op (first ops)]
        (let [{:keys [next-state expect]} (run-model state op)
              actual (run-actual op)]
          ;(prn op "\n" expect actual (= expect actual))
          (if (= expect actual)
            (recur next-state (rest ops))
            false)) ; Test Failed!
        true))))    ; Test Success: All ops are runned succesfully!

;;;
(comment
  #_(require '[babashka.fs :as fs])
  #_(str/join " " ;; To know used characters
              (->> (fs/list-dir "/home/dev/outer-brain/thinks/")
                   (map #(-> % str slurp set))
                   (apply clojure.set/union) (sort)))

  (def id:post (last (g/sample (s/gen ::id:post))))

  ;;; gens & specs
  (g/sample (s/gen ::md-text) 30)
  (g/sample (s/gen ::post) 30)
  (g/sample (s/gen ::id:post) 10)

  (url "http" "localhost" 8384 "")
  (g/sample (gen-valid-url id:post) 20)
  (g/sample (gen-invalid-url id:post) 20)

  ;;; Operations
  (url-path-set id:post) ;; NOTE: unit test for reader/url-path-set

  (g/sample (gen-create id:post) 10)
  (g/sample (gen-read id:post) 20)
  (g/sample (gen-delete id:post) 10)
  #_(g/sample gen-num-publics)

  (g/sample (gen-ops id:post) 20)

  ;;; Runners
  ;(run-model state (last (g/sample (gen-ops state) 20)))
  (model-test))
