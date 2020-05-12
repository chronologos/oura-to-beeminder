(ns reframe-ring-backend.core
  (:require

    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.cors :refer [wrap-cors]]))



(use 'ring.adapter.jetty)

(defn wrapped-handler [routes]
  (wrap-cors routes :access-control-allow-origin [#"http://localhost:8280"]
             :access-control-allow-methods [:get :put :post :delete]))

(defroutes app
           (GET "/" [] "<h1>Hello World</h1>")
           (route/not-found "<h1>Page not found</h1>"))

(defn -main []
  (run-jetty (wrapped-handler app) {:port  3000
                                    :join? false}))
