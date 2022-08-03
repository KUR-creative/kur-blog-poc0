(ns kur.util.file-system
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]))

;;;
(s/def ::file-name ;; not a root
  (s/and string? #(not (#{"" "." ".."} %)) #(not (.contains % "/"))))

(s/def ::path (s/and string? #(not= % "")))
;; NOTE: Valid unix path are way too robust.
;; See https://unix.stackexchange.com/questions/125522/path-syntax-rules
;; Maybe . .. / /// ~ etc.. are need to be supported. But not now!

(s/def ::extension
  (s/and string? #(not (.contains % "/")) #(not= (first %) \.)))

(s/def ::existing-path (s/and ::path fs/exists?))

;;;
(defn delete-all-except-gitkeep [dir]
  (->> (fs/list-dir dir)
       (remove #(= (fs/file-name %) ".gitkeep"))
       (run! fs/delete)))