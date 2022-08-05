(ns kur.blog.state-test
  (:require [clojure.test :refer [is deftest]]
            [kur.blog.state :refer :all :as state]))

(deftest happened-test
  (let [info {:id "k1234567890"}]
    (is (thrown? AssertionError (happened nil nil)))
    (is (= (happened nil info)  ::state/create))
    (is (= (happened info nil)  ::state/delete))
    (is (= (happened info info) ::state/as-is))
    (is (= (happened info (assoc info ::public? true)) ::state/update))))

(deftest happeneds-test
  (let [old-state {:0 0 :1 1 :2 2 :3 3 :4 4}
        new-state                {:3 3 :4 44 :5 5 :6 6 :7 7}]
    (happeneds old-state new-state)))