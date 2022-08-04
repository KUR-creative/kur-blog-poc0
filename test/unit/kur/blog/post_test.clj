(ns kur.blog.post-test
  (:require [babashka.fs :as fs]
            [clojure.spec.alpha :as s]
            [clojure.test :refer [is deftest testing run-tests]]
            [kur.blog.post :refer :all :as post]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :refer [for-all] :rename {for-all defp}]))

(deftest file-info-regression-test
  (is (= (file-info "not-exists") {}))
  (is (= (mapv file-info (sort (fs/list-dir "test/fixture/blog-v1-md")))
         [#:kur.blog.post{:id "kur2004250001",
                          :meta-str "-",
                          :title "오버 띵킹의 함정을 조심하라",
                          :public? nil,
                          :path "test/fixture/blog-v1-md/kur2004250001.-.오버 띵킹의 함정을 조심하라.md",
                          ;:exists? true,
                          :last-modified-millis 1649905086389}
          #:kur.blog.post{:id "kur2205182112",
                          :title ".인간의 우열(편차)보다 사용하는 도구와 환경, 문화의 우열이 퍼포먼스에 더 큰 영향을 미친다",
                          :public? nil,
                          :path "test/fixture/blog-v1-md/kur2205182112..인간의 우열(편차)보다 사용하는 도구와 환경, 문화의 우열이 퍼포먼스에 더 큰 영향을 미친다.md",
                          ;:exists? true,
                          :last-modified-millis 1658968984405}
          #:kur.blog.post{:id "kur2206082055",
                          :title "Clojure 1.10의 tap은 디버깅 용도(better prn)로 사용할 수 있다",
                          :public? nil,
                          :path "test/fixture/blog-v1-md/kur2206082055.Clojure 1.10의 tap은 디버깅 용도(better prn)로 사용할 수 있다.md",
                          ;:exists? true,
                          :last-modified-millis 1655470637675}
          #:kur.blog.post{:id "kur2207111708",
                          :title
                          "Secret Manager 서비스는 어플리케이션의 secret을 안전하게 관리하여.. haha. 하드코딩된 secret을 없애고 주기적으로 자동화된 secret 변경이 가능케 한다",
                          :public? nil,
                          :path
                          "test/fixture/blog-v1-md/kur2207111708.Secret Manager 서비스는 어플리케이션의 secret을 안전하게 관리하여.. haha. 하드코딩된 secret을 없애고 주기적으로 자동화된 secret 변경이 가능케 한다.md",
                          ;:exists? true,
                          :last-modified-millis 1657527967700}
          #:kur.blog.post{:id "kur2207161305",
                          :meta-str "+",
                          :title "kill-current-sexp의 Emacs, VSCode 구현",
                          :public? true,
                          :path "test/fixture/blog-v1-md/kur2207161305.+.kill-current-sexp의 Emacs, VSCode 구현.md",
                          ;:exists? true,
                          :last-modified-millis 1656926109907}
          #:kur.blog.post{:id "kur2207281052",
                          :meta-str "+",
                          :public? true,
                          :path "test/fixture/blog-v1-md/kur2207281052.+.md",
                          ;:exists? true,
                          :last-modified-millis 1658973176383}])))

(defspec fname-parts-roundtrip-test 1000
  (defp [parts (s/gen ::post/file-name-parts)]
    (= parts (fname->parts (parts->fname parts)))))