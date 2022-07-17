(ns kur.util.regex)

(def alphanumeric  #"[a-zA-Z0-9]")
(def alphanumeric* #"[a-zA-Z0-9]*")
(def alphanumeric+ #"[a-zA-Z0-9]+")
(def hangul  #"[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]")
(def hangul* #"[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*")
(def hangul+ #"[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]+")

(comment
  (require '[clojure.spec.gen.alpha :as sg]
           '[com.gfredericks.test.chuck.generators :as ug])
  (sg/sample (ug/string-from-regex alphanumeric ) 30)
  (sg/sample (ug/string-from-regex alphanumeric*) 30)
  (sg/sample (ug/string-from-regex alphanumeric+) 30)
  (sg/sample (ug/string-from-regex hangul ) 30)
  (sg/sample (ug/string-from-regex hangul*) 30)
  (sg/sample (ug/string-from-regex hangul+) 30)
  )