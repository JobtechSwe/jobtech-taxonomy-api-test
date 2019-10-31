(ns jobtech-taxonomy-api-test.core-test
  (:require [clojure.test :refer :all]
            [jobtech-taxonomy-api-test.core :refer :all]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            ))


(def api-key (System/getenv "JOBTECH_TAXONOMY_API_KEY"))

(defn get-filename-v2-from-type [type]
  (str "resources/" type "_v2.json")
  )


(def url (str (System/getenv "JOBTECH_TAXONOMY_API_URL")  "v1/taxonomy/main/concepts") )




(defn parse-local-preferred-labels [filename]
  (map :term (parse-string (slurp filename) true )))


(defn call-api [preferredLabel type url]
  (client/get url {:as :json-strict
                   :headers {"api-key" api-key}
   ;;                :debug true
                   :query-params {"preferred-label" preferredLabel
                                  "type" type
                                  }})
  )

(defn get-concept [preferredLabel type url]
  (:body (call-api preferredLabel type url))
  )


(defn get-remote-preferredLabel [preferredLabelLocal type url]
  [preferredLabelLocal (:taxonomy/preferred-label (first (get-concept preferredLabelLocal type url)))]
  )


(defn get-remote-and-local-values [type local-preferred-labels url]
  (map #(get-remote-preferredLabel % type url) local-preferred-labels)
  )


(defn is-local-equal-to-remote [type file url]
  (let [
        result (get-remote-and-local-values type (parse-local-preferred-labels file) url)]


    (doall (map (fn [[expected actual]]
                  (is (= expected actual))
                  )  result ))
    )
  )


(defn test-taxonomy [name type file url]
  (testing name
    (is-local-equal-to-remote type file url)
    )
  )

(def types
  [
   "continent",
   "country",
   "driving-licence",
 ;;  "employment_duration",
 ;;  "employment_type",
 ;;  "isco_level_1",
 ;;  "isco_level_4",
   "keyword",
   "language",
   "language-level",
   "municipality",
;;   "occupation_collection",
   "occupation-field",
   "occupation-name",
   "region",
   "skill",
   "skill-headline",
;;   "sni_level_1",
;;  "sni_level_2",
   "ssyk-level-1",
   "ssyk-level-2",
   "ssyk-level-3",
   "ssyk-level-4",
;;   "sun_education_field_1",
;;   "sun_education_field_2",
;;   "sun_education_field_3",
;;   "sun_education_field_4",
;;   "sun_education_level_1",
;;   "sun_education_level_2",
;;   "sun_education_level_3",
;;   "wage_type",
;;   "worktime_extent"
   ]
  )

(def types-short
  [
   ;;"driving-licence"
   ;;"language-level"
   "country"
   ]
  )

(defn run-taxonomy-tests [types url]
  (doall (map (fn [type]
                (test-taxonomy (str type "--" url)
                               type
                               (get-filename-v2-from-type type)
                               url))
              types
              )))


(deftest test-all
  (do
    (run-taxonomy-tests types url)
    )
  )


(defn v67-file []
  (parse-string (slurp "resources/concept_to_taxonomy_v1.json") true ))


(defn call-api-with-concept-id [concept-id url]
  (client/get url {:accept :json
                   :as :auto
                   :headers {"api-key" api-key}
                   :query-params {"id" concept-id
                                  "version" 1
                                  }})
  )

(defn preferred-label-from-api-call [concept-id url]
  (:taxonomy/preferred-label (first (:body (call-api-with-concept-id concept-id url))))
  )


(defn has-unwanted-type [type]
  (contains? #{"place"
               "sun-education-field-3"
               "sun-education-field-2"
               "sun-education-field-1"
               "sun-education-level-3"
               "sun-education-level-2"
               "sun-education-level-1"
               "deprecated-education-field"
               "deprecated-education-level"
               "occupation-experience-years"
               } type)
  )

(defn call-api-with-v67-test-data [url]
  (map (fn [[concept-id concept]]
         (let [expected (:label concept)
               actual  (preferred-label-from-api-call (name concept-id) url)
               ]
           [expected actual]
           )

         )
       (filter (fn [[id con]]   (not (has-unwanted-type (:type con)))  ) (v67-file) )

       )
  )

(defn is-local-equal-to-remote-v67 [url]

  (let [result (call-api-with-v67-test-data url)]

    (doall (map (fn [[expected actual]]
                  (is (= expected actual))
                  )  result )))
  )


(deftest test-version-67
  (testing "testing version 67 develop"
    (is-local-equal-to-remote-v67 url)
    )
  )
