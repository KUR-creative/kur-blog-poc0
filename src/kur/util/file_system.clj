(ns kur.util.file-system
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]))

;;;
(s/def ::file-name ;; not a root
  (s/and #(not (#{"" "." ".."} (str %)))
         #(not (.contains (str %) "/"))))

(s/def ::path #(not= (str %) ""))
;; NOTE: Valid unix path are way too robust.
;; See https://unix.stackexchange.com/questions/125522/path-syntax-rules
;; Maybe . .. / /// ~ etc.. are need to be supported. But not now!

(s/def ::extension
  (s/and #(not (.contains (str %) "/")) #(not= (first (str %)) \.)))

(s/def ::existing-path (s/and ::path fs/exists?))

;;;
(defn delete-all-except-gitkeep [dir]
  (->> (fs/list-dir dir)
       (remove #(= (fs/file-name %) ".gitkeep"))
       (run! fs/delete)))