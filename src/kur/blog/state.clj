(ns kur.blog.state
  (:require
   [clojure.spec.alpha :as s]
   [kur.blog.post :as post]))

(s/def ::state (s/map-of ::post/id map?)) ;TODO? map? = post/file-info

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