(ns jobtech-taxonomy-api-test.legacy-converter
  (:require [clojure.test :refer :all]
            [jobtech-taxonomy-api-test.core :refer :all]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            [jobtech-taxonomy-api-test.core-test :as ct]
            [jobtech-taxonomy-api-test.smoke-test :as st]
            [jobtech-taxonomy-api-test.config :as config]
            ))

(def legacy-base-url (str config/base-url "/v1/taxonomy/legacy/"))


;; GET /v1/taxonomy/legacy/convert-matching-component-id-to-new-id


(defn call-api-legacy [endpoint query-params]
  (st/call-api (str legacy-base-url endpoint) query-params))

(defn call-convert-matching-component-id-to-new-id [id type]
  (call-api-legacy "convert-matching-component-id-to-new-id" {"matching-component-id" id
                                                              "type" type}))

(def types-to-test
  #{"continent",
    "country",
    "driving-licence",
    "employment-duration",
    "employment-type",
    "language",
    "language-level",
    "municipality",
    "occupation-collection",
    "occupation-field",
    "occupation-name",
    "occupation-group"
    "region",
    "skill",
    "skill-headline",
    "sni-level-1",
    "sni-level-2",
    "ssyk-level-4",
    "wage-type",
    "worktime-extent"})

(defn filter-fun-for-test-data [data]
  (contains? types-to-test (get-in data [0 :type])))

(defn filter-test-data [data]
  (filter filter-fun-for-test-data data))

(defn load-test-data []
  (-> (ct/v67-file)
      (clojure.set/map-invert)
      (filter-test-data)))

(defn change-occupation-group-to-ssyk-level-4 [string]
  (case string
    "occupation-group" "ssyk-level-4"
    string))

(defn call-api-with-test-data [data]
  (let [[legacy-data concept-id] data
        {:keys [legacyAmsTaxonomyId type]}  legacy-data
        remote-concept-id  (:taxonomy/id (call-convert-matching-component-id-to-new-id legacyAmsTaxonomyId (change-occupation-group-to-ssyk-level-4 type)))]
    [(name concept-id) remote-concept-id]))

(defn  is-local-equal-to-remote-v67-matching-component-converter []
  (let [result (pmap call-api-with-test-data (load-test-data))]
    (doall (pmap (fn [[expected actual]]
                   (is (= expected actual)))  result))))

(deftest test-matchning-component-converter
  (testing "Testing matchning converter endpoint"
    (is-local-equal-to-remote-v67-matching-component-converter)))
