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

(deftest next-state-test
  (let [old {:-2 -2   :-1 -1 :0 0  :1 1   :2 2 :3 3}
        new {:-2 -20               :1 10  :2 2 :3 3  :4 400 :5 500}
        haps {:-2 ::state/update, :-1 ::state/delete, :0 ::state/delete,
              :1  ::state/update, :2  ::state/as-is,  :3  ::state/as-is,
              :4  ::state/create, :5  ::state/create}]
    (is (= (next-state old new haps)
           {:-2 -20               :1 10  :2 2 :3 3  :4 400 :5 500}))))