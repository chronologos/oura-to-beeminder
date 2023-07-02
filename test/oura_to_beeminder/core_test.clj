(ns oura-to-beeminder.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.walk :refer [keywordize-keys]]
            [oura-to-beeminder.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))


(deftest should-sync-trivial
  (testing "should sync"
    (let [x (should-sync {} {})]
      (is (true?
           x)))))

(deftest should-sync-trivial
  (testing "should sync"
    (let [x (should-sync {} {})]
      (is (true?
           x)))))

(deftest oura-processed-spec
  (testing "oura spec"
    (let [x (get-oura-sleep-data
             (fn [x] {:body
                      "{\"data\":[{\"id\":\"9b1d962f-4abb-4e4d-9c35-e338d0866c54\",\"contributors\":{\"deep_sleep\":94,\"efficiency\":95,\"latency\":86,\"rem_sleep\":97,\"restfulness\":76,\"timing\":90,\"total_sleep\":77},\"day\":\"2023-06-27\",\"score\":84,\"timestamp\":\"2023-06-27T00:00:00+00:00\"},{\"id\":\"0b4c4d0c-8c5b-4e76-ac47-344a8e47beb2\",\"contributors\":{\"deep_sleep\":95,\"efficiency\":83,\"latency\":78,\"rem_sleep\":88,\"restfulness\":70,\"timing\":85,\"total_sleep\":82},\"day\":\"2023-06-28\",\"score\":82,\"timestamp\":\"2023-06-28T00:00:00+00:00\"},{\"id\":\"11449190-2d1c-4bc7-8ba7-c368d86e41a7\",\"contributors\":{\"deep_sleep\":78,\"efficiency\":93,\"latency\":94,\"rem_sleep\":96,\"restfulness\":71,\"timing\":92,\"total_sleep\":80},\"day\":\"2023-06-29\",\"score\":84,\"timestamp\":\"2023-06-29T00:00:00+00:00\"},{\"id\":\"1af68bc1-9fc7-4d3c-b48b-a96ea3b562a9\",\"contributors\":{\"deep_sleep\":96,\"efficiency\":97,\"latency\":83,\"rem_sleep\":75,\"restfulness\":71,\"timing\":100,\"total_sleep\":66},\"day\":\"2023-06-30\",\"score\":79,\"timestamp\":\"2023-06-30T00:00:00+00:00\"},{\"id\":\"faec13b8-5f9f-4f28-bbfa-fcf40b5603dd\",\"contributors\":{\"deep_sleep\":96,\"efficiency\":96,\"latency\":83,\"rem_sleep\":92,\"restfulness\":75,\"timing\":100,\"total_sleep\":81},\"day\":\"2023-07-01\",\"score\":86,\"timestamp\":\"2023-07-01T00:00:00+00:00\"},{\"id\":\"164ade8a-3960-4853-9913-432b0224a9b3\",\"contributors\":{\"deep_sleep\":96,\"efficiency\":98,\"latency\":78,\"rem_sleep\":92,\"restfulness\":79,\"timing\":86,\"total_sleep\":83},\"day\":\"2023-07-02\",\"score\":86,\"timestamp\":\"2023-07-02T00:00:00+00:00\"}],\"next_token\":null}"})  2)]
      (is (true?
           (s/valid? :oura/sleep-data x))
          (s/explain-str :oura/sleep-data x)))))

(deftest oura-http-spec
  (testing "oura http spec"
    (let [x (oura-httpfetcher 2)]
      (is (true?
           (s/valid? :oura/http-raw-data x))
          (s/explain-str :oura/http-raw-data x)))))

(deftest bmdr-processed-spec
  (testing "bmdr spec"
    (let [x (get-bmdr
             (fn [x] {:body
                      "[{\"timestamp\":1688367599,\"value\":86.0,\"comment\":\"autosync\",\"id\":\"64a1eb2ef0168a8e7bb396a5\",\"updated_at\":1688333102,\"requestid\":null,\"canonical\":\"02 86 \\\"autosync\\\"\",\"fulltext\":\"2023-Jul-02 entered at 14:25 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230702\"},{\"timestamp\":1688281199,\"value\":86.0,\"comment\":\"autosync\",\"id\":\"64a1eb2df0168a8e67b3af76\",\"updated_at\":1688333101,\"requestid\":null,\"canonical\":\"01 86 \\\"autosync\\\"\",\"fulltext\":\"2023-Jul-01 entered at 14:25 on 2023-Jul-02 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230701\"},{\"timestamp\":1688194799,\"value\":79.0,\"comment\":\"autosync\",\"id\":\"64a1eb2cf0168a8ea3b38e4b\",\"updated_at\":1688333100,\"requestid\":null,\"canonical\":\"30 79 \\\"autosync\\\"\",\"fulltext\":\"2023-Jun-30 entered at 14:25 on 2023-Jul-02 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230630\"},{\"timestamp\":1688108399,\"value\":84.0,\"comment\":\"autosync\",\"id\":\"64a1eb2cf0168a8e67b3af75\",\"updated_at\":1688333100,\"requestid\":null,\"canonical\":\"29 84 \\\"autosync\\\"\",\"fulltext\":\"2023-Jun-29 entered at 14:25 on 2023-Jul-02 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230629\"},{\"timestamp\":1688021999,\"value\":82.0,\"comment\":\"autosync\",\"id\":\"64a1eb2bf0168a8e67b3af74\",\"updated_at\":1688333099,\"requestid\":null,\"canonical\":\"28 82 \\\"autosync\\\"\",\"fulltext\":\"2023-Jun-28 entered at 14:24 on 2023-Jul-02 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230628\"},{\"timestamp\":1687935599,\"value\":84.0,\"comment\":\"autosync\",\"id\":\"64a1eb2af0168a8e67b3af73\",\"updated_at\":1688333098,\"requestid\":null,\"canonical\":\"27 84 \\\"autosync\\\"\",\"fulltext\":\"2023-Jun-27 entered at 14:24 on 2023-Jul-02 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230627\"},{\"timestamp\":1687849199,\"value\":82.0,\"comment\":\"autosync\",\"id\":\"64a1eb29f0168a8e67b3af72\",\"updated_at\":1688333097,\"requestid\":null,\"canonical\":\"26 82 \\\"autosync\\\"\",\"fulltext\":\"2023-Jun-26 entered at 14:24 on 2023-Jul-02 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230626\"},{\"timestamp\":1687762799,\"value\":82.0,\"comment\":\"autosync\",\"id\":\"64a1eb28f0168a8e67b3af71\",\"updated_at\":1688333096,\"requestid\":null,\"canonical\":\"25 82 \\\"autosync\\\"\",\"fulltext\":\"2023-Jun-25 entered at 14:24 on 2023-Jul-02 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230625\"},{\"timestamp\":1687676399,\"value\":83.0,\"comment\":\"autosync\",\"id\":\"64a1eb27f0168a8e7bb396a4\",\"updated_at\":1688333095,\"requestid\":null,\"canonical\":\"24 83 \\\"autosync\\\"\",\"fulltext\":\"2023-Jun-24 entered at 14:24 on 2023-Jul-02 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230624\"},{\"timestamp\":1687589999,\"value\":82.0,\"comment\":\"autosync\",\"id\":\"64a1eb27f0168a8e7bb396a3\",\"updated_at\":1688333095,\"requestid\":null,\"canonical\":\"23 82 \\\"autosync\\\"\",\"fulltext\":\"2023-Jun-23 entered at 14:24 on 2023-Jul-02 by ulysses9 via api\",\"origin\":\"api\",\"creator\":\"ulysses9\",\"daystamp\":\"20230623\"}]"}) 2)]
      (is (true?
           (s/valid? :bmdr/data x))
          (s/explain-str :bmdr/data x)))))

(deftest bmdr-http-spec
  (testing "bmdr http spec"
    (let [x (bmdr-httpfetcher 2)]
      (is (true?
           (s/valid? :bmdr/http-raw-data x))
          (s/explain-str :bmdr/http-raw-data x)))))