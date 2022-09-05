(ns kur.blog.resource
  "Resource entity.

   Currently, entity only consists of 2 types
   - post(md file in md-dir)
   - resource(other extension)

   If you need more than 2 types, consider introducing polymorphism"
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]
            [kur.util.file-system :as uf]))

;; NOTE: The following is same with kur.blog.post
(defn file-info [path]
  ;(prn (s/valid? ::uf/existing-path path))
  (if (s/valid? ::uf/existing-path path)
    {::id path
     ::last-modified-millis (uf/last-modified-millis path)
     :public? true}
    {}))

(defn id:file-info [dir & dirs] ;; NOTE: Same with post/
  (let [infos (->> (fs/list-dirs (cons dir dirs) "*")
                   (map str) (map file-info) (filter seq))]
    (zipmap (map ::id infos) infos)))

;;
(comment
  (def dir "md2x") (def dirs nil)
  (file-info "README.md")
  (id:file-info "md2x"))