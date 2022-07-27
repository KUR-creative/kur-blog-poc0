(ns kur.util.file-system
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sg]))

(s/def ::file-name ;; not a root
  (s/and string? #(not (#{"" "." ".."} %)) #(not (.contains % "/"))))
(sg/sample (s/gen ::file-name))

(s/def ::path (s/and string? #(not= % "")))
;; NOTE: Valid unix path are way too robust.
;; See https://unix.stackexchange.com/questions/125522/path-syntax-rules
;; Maybe . .. / /// ~ etc.. are need to be supported. But not now!

(s/def ::extension
  (s/and string? #(not (.contains % "/")) #(not= (first %) \.)))

(s/def ::existing-path (s/and ::path fs/exists?))