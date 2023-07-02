(ns oura-to-beeminder.core
  (:require
   [compojure.core :refer :all]
   [clojure.set :refer [rename-keys]]
   [clojure.walk :refer [keywordize-keys]]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [clojure.tools.logging :as log]
   [environ.core :refer [env]]
   [clojure.spec.alpha :as s])
  (:import (java.text SimpleDateFormat)
           (java.util Calendar Date))
  (:gen-class))

;; Spec to validate api.ouraring.com/v2/usercollection/daily_sleep
(s/def :oura/sleep-datum (s/keys :req-un [:oura/score :oura/daystamp]))
(s/def :oura/sleep-data (s/coll-of :oura/sleep-datum))
(s/def :oura/body string?)
(s/def :oura/http-raw-data (s/keys :req-un [:oura/body]))

(s/def :bmdr/datum (s/keys :req-un [:bmdr/daystamp :bmdr/value :bmdr/comment]))
(s/def :bmdr/data (s/coll-of :bmdr/datum))
(s/def :bmdr/body string?)
(s/def :bmdr/http-raw-data (s/keys :req-un [:bmdr/body]))

(def oura-token
  (env :oura-token))

(def beeminder-token
  (env :beeminder-token))

(def beeminder-user
  (env :beeminder-user))

(def beeminder-goal-id
  (env :beeminder-goal-id))

;; Oura data will be synced on next day, so subtract one day to get the previous night's sleep.
(defn now-oura-format [date offset] (let [cal (Calendar/getInstance)
                                          _ (.setTime cal date)
                                          _ (.add cal Calendar/DATE (- 0 offset))
                                          current-date-minus-one (.getTime cal)]
                                      (.format (SimpleDateFormat. "yyyy-MM-dd") current-date-minus-one)))

(defn bmdr-extract-date [date-str]
  (let [date (.parse (SimpleDateFormat. "yyyyMMdd") (get (.split date-str "T") 0))
        formatter (SimpleDateFormat. "yyyy-MM-dd")]
    (.format formatter date)))

;; 2023-06-27T00:00:00+00:00 -> 2023-06-27
(defn oura-extract-date [date-str]
  (let [date (.parse (SimpleDateFormat. "yyyy-MM-dd") date-str)
        formatter (SimpleDateFormat. "yyyy-MM-dd")]
    (.format formatter date)))

(defn oura-httpfetcher [lookback-days]
  (log/debug (now-oura-format (Date.) lookback-days))
  (log/debug oura-token)
  (keywordize-keys
   (client/get "https://api.ouraring.com/v2/usercollection/daily_sleep"
               {:query-params {"start_date" (now-oura-format (Date.) lookback-days)
                              ;"end" "2020-05-19"
                               }
                :headers {"Authorization" (str "Bearer " oura-token)}})))

(defn get-oura-sleep-data [httpfetcher lookback-days]
  (let [raw (httpfetcher lookback-days)
        res (-> raw
                  ;;  #(log/debug "oura-data" %)
                :body
                json/read-str
                (get "data"))]
    (->> res
         (mapv keywordize-keys)
         (mapv #(select-keys % [:score :timestamp]))
         (mapv #(rename-keys % {:timestamp :daystamp}))
         (mapv #(update % :daystamp oura-extract-date)))))

(defn bmdr-httpfetcher [count]
  (log/debug beeminder-token)
  (client/get (str "https://www.beeminder.com/api/v1/users/ulysses9/goals/"  beeminder-goal-id "/datapoints.json")
              {:query-params {"count" count
                              "auth_token" beeminder-token}}))

(defn get-bmdr [httpfetcher count]
  (let [raw (httpfetcher count)
        res (-> raw
                :body
                json/read-str)]
    (->> res
         (mapv keywordize-keys)
         (mapv #(update % :daystamp bmdr-extract-date)))))

(defn update-bmdr-data [daystamp val comment]
  (log/debug "update-bmdr-data" daystamp val comment)
  (client/post (str "https://www.beeminder.com/api/v1/users/" beeminder-user "/goals/" beeminder-goal-id "/datapoints.json")
               {:form-params {"auth_token" beeminder-token
                              "daystamp" daystamp
                              "value" val
                              "comment" comment}}))

(defn should-sync [oura bmdr-data]
  (let [oura-date (:daystamp oura)]
    (not-any? #(= (:daystamp %) oura-date) bmdr-data)))

(defn get-all-data [lookback-days]
  (let [bmdr-data (get-bmdr bmdr-httpfetcher 30)
        _ (log/debug "bmdr-data-mod" bmdr-data)
        oura-data (get-oura-sleep-data oura-httpfetcher (dec lookback-days))
        _ (log/debug "oura-data" oura-data)]
    (as-> oura-data d
      (filterv #(should-sync % bmdr-data) d)
      (mapv #(update-bmdr-data (:daystamp %) (:score %) "autosync") d))))

(defn -main [] (if (or (= beeminder-goal-id nil) (= beeminder-user nil) (= beeminder-token nil) (= oura-token nil))
                 (prn "one or more env vars not defined")
                 (get-all-data 10)))