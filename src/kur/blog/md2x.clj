(ns kur.blog.md2x
  "Convert markdown to someting(x)"
  (:require [clojure.java.io :as io])
  (:import (com.eclipsesource.v8 NodeJS)))

;;
#_(defonce nodejs-runtime (NodeJS/createNodeJS))
#_(defonce md2x (.require nodejs-runtime (io/file md2x-path)))

#_(defn obsidian-html [md]
    (.executeJSFunction md2x "obsidian" (to-array [md])))

(def md2x-path "./md2x/out/md2x.js")
(defn obsidian-html [md]
  @(future (let [rt (NodeJS/createNodeJS)
                 md2x (.require rt (io/file md2x-path))]
             (.executeJSFunction md2x "obsidian" (to-array [md])))))

;;
(comment
  (obsidian-html "### 3")
  (obsidian-html (slurp "./README.md"))
  (spit "out/t.html" (obsidian-html (slurp "./README.md"))))