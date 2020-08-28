(ns oura-to-beeminder.core
  (:require
    [compojure.core :refer :all]
    [clj-http.client :as client]
    [clojure.data.json :as json]
    [clojure.tools.logging :as log]
    [environ.core :refer [env]]
    [clojure.spec.alpha :as s])
  (:import (java.text SimpleDateFormat)
           (java.util Calendar Date))
  (:gen-class))

(def oura-token
  (env :oura-token))

(def beeminder-token
  (env :beeminder-token))

(def beeminder-user
  (env :beeminder-user))

(def beeminder-goal-id
  (env :beeminder-goal-id))

;; Oura data will be synced on next day, so subtract one day to get the previous night's sleep.
(defn now-oura-format [date offset] (let [ cal (Calendar/getInstance)
                                          _ (.setTime cal date)
                                          _ (.add cal Calendar/DATE (- 0 offset))
                                          current-date-minus-one (.getTime cal)]
                                      (.format (SimpleDateFormat. "yyyy-MM-dd") current-date-minus-one)))

(defn bmdr-time-str-to-oura [date-str]
  (let [date (.parse (SimpleDateFormat. "yyyyMMdd") date-str)
        formatter (SimpleDateFormat. "yyyy-MM-dd")]
    (.format formatter date)))

(defn get-oura-data [lookback-days]
  (log/debug (now-oura-format (Date.) lookback-days))
  (log/debug oura-token)
  (client/get "https://api.ouraring.com/v1/sleep"
              {:query-params {
                              "start" (now-oura-format (Date.) lookback-days)
                              ;"end" "2020-05-19"
                              "access_token" oura-token}}))

(defn get-oura-sleep-data [lookback-days]
  (mapv #(select-keys % ["score" "summary_date"])
        (->
          (get-oura-data lookback-days)
          :body
          json/read-str
          (get "sleep"))))

(defn get-bmdr-data []
  (log/debug beeminder-token)
  (client/get (str "https://www.beeminder.com/api/v1/users/ulysses9/goals/"  beeminder-goal-id ".json")
              {:query-params {
                              "count" 10
                              "auth_token" beeminder-token}}))

(defn update-bmdr-data [daystamp val comment]
  (client/post (str "https://www.beeminder.com/api/v1/users/" beeminder-user "/goals/" beeminder-goal-id "/datapoints.json")
               {:form-params {"auth_token" beeminder-token
                              "daystamp" daystamp
                              "value" val
                              "comment" comment}}))

(defn should-sync [oura bmdr-data]
  (let [oura-date (get oura "summary_date")]
    (not-any? #(= (get % "daystamp") oura-date) bmdr-data)))

(defn get-all-data [lookback-days]
  ( let [bmdr-data (->
                     (get-bmdr-data)
                     :body
                     json/read-str
                     ;; apparently max number of points here is 5
                     (get "recent_data"))
         bmdr-data-mod (mapv #(update % "daystamp" bmdr-time-str-to-oura) bmdr-data)
         oura-data (get-oura-sleep-data lookback-days)]
    (as-> oura-data d
          (filterv #(should-sync % bmdr-data-mod) d)
          (mapv #(update-bmdr-data (get % "summary_date") (get % "score") "autosync") d))))

(defn -main [] (if (or (= beeminder-goal-id nil) (= beeminder-user nil) (= beeminder-token nil) (= oura-token nil))
                 (prn "one or more env vars not defined")
                 (get-all-data 4)))