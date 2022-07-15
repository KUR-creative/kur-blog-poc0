(ns kur.blog.write
  "Writes posts of blog from input md and resource"
  (:require [clojure.java.io :as io])
  (:import (com.eclipsesource.v8 NodeJS)))

(defonce nodejs-runtime (NodeJS/createNodeJS))
(def md2x-path "./md2x/out/md2x.js")
(def md2x (.require nodejs-runtime (io/file md2x-path)))
(defn obsidian-html [md]
  (.executeJSFunction md2x "obsidian" (to-array [md])))

(comment
  (obsidian-html "### 3")
  (obsidian-html (slurp "./README.md"))
  (spit "out/t.html" (obsidian-html (slurp "./README.md")))
  )