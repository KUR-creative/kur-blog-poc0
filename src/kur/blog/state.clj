(ns kur.blog.state
  (:require
   [clojure.spec.alpha :as s]
   [kur.blog.post :as post]
   [kur.blog.resource :as resource]
   [kur.util.file-system :as uf]))

(s/def ::state
  (s/map-of (s/or ::post/id ::uf/path) map?)) ;TODO? map? = post/file-info

(defn state ; TODO: Remove business logic using polymolphism?
  "Create state"
  [md-dir css-dir]
  (merge (post/id:file-info md-dir)
         (resource/id:file-info css-dir)))

(defn happened [old-info new-info]
  {:pre [(or old-info new-info)]}
  (cond (nil? old-info)       ::create
        (nil? new-info)       ::delete
        (= old-info new-info) ::as-is
        :else                 ::update))

(defn happeneds [old new]
  (let [all-ks (apply conj (set (keys old)) (keys new))]
    (zipmap all-ks
            (map happened (map old all-ks) (map new all-ks)))))

(defn next-state [old new happeneds]
  (let [deleted-keys (->> happeneds
                          (filter #(= (val %) ::delete))
                          (map #(key %)))]
    (apply dissoc (merge old new) deleted-keys)))