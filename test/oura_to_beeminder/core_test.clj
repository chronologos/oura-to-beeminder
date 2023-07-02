(ns oura-to-beeminder.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [oura-to-beeminder.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest oura-processed-spec
  (testing "oura spec"
    (let [x (get-oura-sleep-data 2)]
      (is (true?
           (s/valid? :oura/sleep-data x))
          (s/explain-str :oura/sleep-data x)))))

(deftest oura-http-spec
  (testing "oura http spec"
    (let [x (get-oura-http 2)]
      (is (true?
           (s/valid? :oura/http-raw-data x))
          (s/explain-str :oura/http-raw-data x)))))