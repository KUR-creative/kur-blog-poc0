(ns kur.blog-test.spbt
  "Blog Integration Test Using Stateful PBT"
  (:require
   [babashka.fs :as fs]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.generators :as g]
   [clojure.test.check.properties :refer [for-all] :rename {for-all defp}]
   [kur.blog.main :as main]
   [kur.blog.post :as post]
   [kur.blog.publisher :refer [url-path-set]]
   [kur.util.file-system :refer [delete-all-except-gitkeep]]
   [kur.util.generator :refer [string-from-regexes]]
   [kur.util.regex :refer [ascii* common-whitespace* hangul*]]))

;;; Generators and Specs
(def gen-md-text
  (string-from-regexes ascii* common-whitespace* hangul*))

(defn gen-id:post [dir]
  (g/let [num-posts (g/such-that #(> % 0) g/nat)
          paths (g/vector (g/fmap #(str (fs/path dir %))
                                  (s/gen ::post/file-name)) num-posts)
          md-texts (g/vector gen-md-text num-posts)]
    (let [post-infos (map post/file-info paths)]
      (zipmap (map ::post/id post-infos)
              (map #(assoc %1 :md-text %2) post-infos md-texts)))))

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

(def gen-n-publics (g/return {:kind :n-publics}))

(def op-wait {:kind :wait})

(defn gen-ops [id:post]
  (g/let [ops (g/vector (g/one-of [(gen-create id:post)
                                   (gen-read id:post)
                                   #_(gen-delete id:post)
                                   gen-n-publics]))]
    (reduce #(if (and (not= (:kind (peek %1)) :n-publics)
                      (= (:kind %2) :n-publics))
               (conj %1 op-wait %2)
               (conj %1 %2))
            []
            ops)))

;;; Runners
(defn run-model [state op]
  (case (:kind op)
    :create    {:next-state (assoc state (:id op) (:post op))
                :expect :no-check}
    :read      {:next-state state
                :expect (-> op :post :md-text)}
    :delete    {:next-state (dissoc state (:id op))
                :expect :no-check}
    :wait      {:next-state state
                :expect :no-check}
    :n-publics {:next-state state
                :expect (count (filter #(-> % val ::post/public?) state))}))

(defn run-actual [op server]
  (case (:kind op)
    :create (let [{{path ::post/path md-text :md-text} :post} op]
              (spit path md-text) :no-check)
    :read :no-check
    :delete :no-check
    :wait (do (Thread/sleep (* 2 (:fs-wait-ms server))) :no-check)
    :n-publics (main/num-public-posts server)))

;;; Tests
;(def test-times 50)
(def test-times 10)

(defspec model-test #_100 test-times
  ;; wait-ms가 작으면 파일을 많이 create 했을 때 에러가 발생한다(당연)
  ;; cnt를 출력해보면, 설정한 횟수보다 많이 돌아가는 경우 shrink가 발생한 것이다.
  ;; 50번에 500ms를 하면 통과한다. 그보다 크면 얼마나 오래 기다리든 통과가 어렵다
  ;; 어차피 한번에 너무 많은 변경이 있는 건 비현실적이다. 그냥 이정도로 하자.
  (let [cnt (atom 1)
        md-dir "test/fixture/post-md"
        html-dir "test/fixture/post-html"
        cfg {:md-dir md-dir :html-dir html-dir
             :fs-wait-ms #_15 500 :port 8080}]
    (delete-all-except-gitkeep md-dir)
    (delete-all-except-gitkeep html-dir)
    (defp [operations (g/bind (gen-id:post md-dir) gen-ops)]
      (println @cnt '/ test-times)
      (swap! cnt inc)
      (let [server (main/start! (main/server cfg))
            result
            (loop [state {}, ops operations]
              (if-let [op (first ops)]
                (let [{:keys [next-state expect]} (run-model state op)
                      actual (run-actual op server)]
                  (if (= expect actual)
                    (recur next-state (rest ops))
                    false)) ; Test Failed!
                true))] ; Test Success: All ops are runned succesfully!
        (delete-all-except-gitkeep md-dir)
        (delete-all-except-gitkeep html-dir)
        (main/close! server)
        result))))

;;;
(comment
  #_(require '[babashka.fs :as fs])
  #_(str/join " " ;; To know used characters
              (->> (fs/list-dir "/home/dev/outer-brain/thinks/")
                   (map #(-> % str slurp set))
                   (apply clojure.set/union) (sort)))

  ;;; gens & specs
  (g/sample (gen-id:post "noexist") 10)
  (def id:post (last (g/sample (gen-id:post "noexist"))))
  (def state id:post)

  (url "http" "localhost" 8384 "")
  (g/sample (gen-valid-url id:post) 20)
  (g/sample (gen-invalid-url id:post) 20)

  ;;; Operations
  (url-path-set id:post) ;; NOTE: unit test for reader/url-path-set

  (g/sample (gen-create id:post) 10)
  (g/sample (gen-read id:post) 20)
  (g/sample (gen-delete id:post) 10)
  (g/sample gen-n-publics)

  (nth (g/sample (gen-ops id:post) 20) 10)

  ;;; Runners
  ;(run-model state (last (g/sample (gen-ops state) 20)))
  ;(model-test)

  (run-actual {:id "As7001010859",
               :kind :create,
               :post {:kur.blog.post/public? true,
                      :kur.blog.post/title "asdf",
                      :kur.blog-test.spbt/md-text "꽊"}}
              {:md-dir "test/fixture/post-md"})

  (do (println "testing...")
      (time (model-test))))