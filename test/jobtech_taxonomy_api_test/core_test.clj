(ns jobtech-taxonomy-api-test.core-test
  (:require [clojure.test :refer :all]
            [jobtech-taxonomy-api-test.core :refer :all]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            ))


(def api-key (System/getenv "JOBTECH_TAXONOMY_API_KEY"))

(def type-ssyk-level-4 "ssyk_level_4")

(def ssyk-level-4-file "resources/ssyk_level_4_v2.json")

(def url "http://jobtech-taxonomy-api-develop.dev.services.jtech.se/v0/taxonomy/public/concepts")

(defn parse-local-preferred-labels [filename]
  (map :term  (parse-string (slurp filename) true )))


(defn call-api [preferredLabel type]
  (client/get url {:accept :json
                   :as :auto
                   :headers {"api-key" api-key}
                   :query-params {"preferredLabel" preferredLabel
                                  "type" type
                                  }})
  )

(defn get-concept [preferredLabel type]
  (:body (call-api preferredLabel type))
  )


(defn get-remote-preferredLabel [preferredLabelLocal type]
  [preferredLabelLocal (:preferredLabel (first (get-concept preferredLabelLocal type)))]
  )


(defn get-remote-and-local-tuples [type local-preferred-labels]

  (map #(get-remote-preferredLabel % type) local-preferred-labels)
  )


(deftest test-ssyk-level-4-labels
  (testing "Testing ssyk level 4"



    (let [[expected actual] (apply map vector
                                   (get-remote-and-local-tuples type-ssyk-level-4
                                                                (parse-local-preferred-labels ssyk-level-4-file)))]
      (is (= expected actual))
      )

    ))
