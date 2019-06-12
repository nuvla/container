(ns sixsq.nuvla.test.login
  (:require
    [clojure.test :refer [is]]
    [clojure.core.async :refer [<!!]]
    [sixsq.nuvla.client.authn :as authn]
    [sixsq.nuvla.test.context :as context]))


(defn tests
  []

  ;; try logging in with incorrect credentials
  (let [response (<!! (authn/login context/client {:href     "session-template/password"
                                                   :username context/nuvla-username
                                                   :password (str context/nuvla-password "-incorrect")}))]

    (is (instance? Exception response))
    (is (= 403 (:status (ex-data response))))
    (is (false? (<!! (authn/authenticated? context/client)))))

  ;; log into the server with correct credentials
  (let [response (<!! (authn/login context/client {:href     "session-template/password"
                                                   :username context/nuvla-username
                                                   :password context/nuvla-password}))]

    (is (= 201 (:status response)))
    (is (re-matches #"session/.+" (:resource-id response)))
    (is (true? (<!! (authn/authenticated? context/client))))))
