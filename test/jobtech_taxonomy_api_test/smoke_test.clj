(ns jobtech-taxonomy-api-test.smoke-test
  (:require [clojure.test :refer :all]
            [jobtech-taxonomy-api-test.core :refer :all]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            [jobtech-taxonomy-api-test.config :as config]
            ))


;; http://jobtech-taxonomy-api-spec-jobtech-taxonomy-api.test.services.jtech.se/v1/taxonomy/swagger-ui/index.html#!/Main/get_v1_taxonomy_main_concept_types


;;(def base-url "http://jobtech-taxonomy-api-spec-jobtech-taxonomy-api.test.services.jtech.se/")

(def main-base-url (str config/base-url "v1/taxonomy/main/"))
(def specific-base-url (str config/base-url "v1/taxonomy/specific/concepts/"))
(def suggesters-base-url (str config/base-url "v1/taxonomy/suggesters/"))

(defn call-api
  ([url query-params]
;;   (println url)
;;   (Println query-params)
   (:body (client/get url
                      (cond->
                          {:as :json-strict
                           :headers {"api-key" config/api-key}
                            :debug false
                           }

                        query-params
                        (assoc :query-params query-params)
                        ))))
  ([url]
   (call-api url nil)
   )
  )

(defn call-api-main
  ([endpoint query-params]
   (call-api (str main-base-url endpoint) query-params))
  ([endpoint]
   (call-api (str main-base-url endpoint))
   )
  )

(defn call-api-specific
  ([endpoint query-params]
   (call-api (str specific-base-url endpoint) query-params))
  ([endpoint]
   (call-api (str specific-base-url endpoint))
   )
  )

(defn call-api-suggesters
  ([endpoint query-params]
   (call-api (str suggesters-base-url endpoint) query-params))
  ([endpoint]
   (call-api (str suggesters-base-url endpoint))
   )
  )



(defn call-concept-types []
  (call-api (str main-base-url "concept/types"))
  )

(defn test-concept-types []
  (is (contains?  (set (call-concept-types)) "skill"))
  )

#_(deftest test-concept-types-test
  (test-concept-types)
  )



(defn call-concepts-animering []
  (call-api-main "concepts" {"preferred-label" "Animering"
                            "type" "skill"
                            })
  )

(defn test-concepts-animering []
  (is (= "dwm2_1V3_MpP" (:taxonomy/id (first (call-concepts-animering))) ))
  )

#_(deftest test-concept-animinering-test
  (test-concepts-animering)
  )

(defn call-changes-old []
  (call-api-main "changes" {"after-version" 1 "limit" 1})
  )

(defn test-changes-old []
  (is (= 2 (:taxonomy/version (first (call-changes-old)))))
  )



(defn call-changes []
  (call-api-main "concept/changes" {"after-version" 1 "limit" 1})
  )


(defn test-changes []
  (is (= 2 (:taxonomy/version (first (call-changes)))))
  )

#_(deftest test-changes-test
  (call-changes)
  )



(defn call-relation-types []
  (call-api-main "relation/types")
  )

(defn test-relation-types []
  (is (contains? (set (call-relation-types))  "broader"))
  )

(defn call-replaced-by-changes []
  (call-api-main "replaced-by-changes" {"after-version" 1})
  )

(defn test-replaced-by-changes []
  (is (= 2 (:taxonomy/version (first (call-replaced-by-changes)))))
  )


(defn call-versions []
  (call-api-main "versions")
  )

(defn test-versions []
  (is (= 1 (:taxonomy/version (first (call-versions)))))
  )



(defn call-country []
  (call-api-specific "country" {"iso-3166-1-alpha-3-2013" "LSO"})
  )

(defn test-country []
  (is (= "LSO" (:taxonomy/iso-3166-1-alpha-3-2013 (first (call-country)))))
  )


(defn call-driving-licence []
  (call-api-specific "driving-licence" {"driving-licence-code-2013" "D"})
  )


(defn test-driving-licence []
  (is (contains?  (set (:taxonomy/implicit-driving-licences (first (call-driving-licence))))  #:taxonomy{:id "4HpY_e2U_TUH", :driving-licence-code-2013 "AM"} ))
  )



(defn call-employment-duration []
  (call-api-specific "employment-duration" {"eures-code-2014" "TF" "limit" 1})
  )

(defn test-employment-duration []
  (is (= "TF" (:taxonomy/eures-code-2014 (first (call-employment-duration)))))
  )


(defn call-isco []
  (call-api-specific "isco" {"isco-code-08" "7544"})
  )

(defn test-isco []
  (is (= "7544" (:taxonomy/isco-code-08 (first (call-isco)))))
  )

(defn call-language []
  (call-api-specific "language" {"iso-639-3-alpha-2-2007" "SD"})
  )

(defn test-language []
  (is (= "SND" (:taxonomy/iso-639-3-alpha-3-2007 (first (call-language)))))
  )

(defn call-region-norway []
  (call-api-specific "region" {"related-ids" "QJgN_Zge_BzJ" "relation" "narrower" })
  )

(defn test-region-norway []
  (= 19 (count (call-region-norway)))
  )

(defn call-region []
  (call-api-specific "region" {"national-nuts-level-3-code-2019" 12})
  )

(defn test-region []
  (is (= "SE224" (:taxonomy/nuts-level-3-code-2013 (first (call-region)))))
  )


(defn call-sni-level []
  (call-api-specific "sni-level" {"sni-level-code-2007" "L"})
  )

(defn test-sni-level []
  (is (= "L" (:taxonomy/sni-level-code-2007 (first (call-sni-level)))))
  )


(defn call-ssyk []
  (call-api-specific "ssyk" {"ssyk-code-2012"  "5132"}))


(defn test-ssyk []
  (is (= "Bartendrar" (:taxonomy/preferred-label (first (call-ssyk))) ))
  )


(defn call-sun-education-field []
  (call-api-specific "sun-education-field" {"sun-education-field-code-2020"  "622b"})
  )

(defn test-sun-education-field []
  (is (= "622b" (:taxonomy/sun-education-field-code-2020 (first (call-sun-education-field)))))
  )


(defn call-sun-education-level []
  (call-api-specific "sun-education-level" {"sun-education-level-code-2020" "537"})
  )

(defn test-sun-education-level []
  (is (= "537"  (:taxonomy/sun-education-level-code-2020 (first (call-sun-education-level)))))
  )




(defn call-autocomplete-aland []
  (call-api-suggesters "autocomplete" {"query-string" "åland ("})
  )

(defn test-autocomplete-aland []
  (is (= "Åland (tillhör Finland)" (:taxonomy/preferred-label (first (call-autocomplete-aland)))))
  )



(defn call-autocomplete-konduktor []
  (call-api-suggesters "autocomplete" {"query-string" "konduktö" "version" 1 "type"  "occupation-name"})
  )

(defn test-autocomplete-konduktor []
  (is (= "Konduktör" (:taxonomy/preferred-label (first (call-autocomplete-konduktor)))))
  )



(defn call-autocomplete-relation-substitutability []

  (call-api-suggesters "autocomplete" {"query-string" "it" "related-ids" "Jus5_Fsv_LTi" "relation" "substitutability-to" })
  )

(defn test-autocomplete-relation-substitutability []
  (is (=  "IT-forensiker"  (:taxonomy/preferred-label  (first (call-autocomplete-relation-substitutability)))))
  )

(defn call-graph-substitutability []
  (call-api-main "graph" {"edge-relation-type" "substitutability"
                          "source-concept-type" "occupation-name"
                          "target-concept-type" "occupation-name"
                          "limit" 1})
  )

(defn test-graph-substitutability []
  (let [sp (get-in (call-graph-substitutability)  [:taxonomy/graph
                                                   :taxonomy/edges
                                                   0
                                                   :taxonomy/substitutability-percentage])]
    (is (or (= 25 sp) (= 75 sp)))
    )
  )


(defn call-main-concept-deprecated-false []
  (call-api-main "concepts" {"type" "employment-type"
                             "deprecated" false
                             })
  )

(defn test-call-main-concept-deprecated-false []
  (is (empty? (filter :taxonomy/deprecated (call-main-concept-deprecated-false))))
  )

(deftest main-tests
  (testing "Main"
    (test-changes)
    (test-versions)
    (test-concept-types)
    (test-concepts-animering)
    (test-relation-types)
    (test-replaced-by-changes)
    (test-call-main-concept-deprecated-false)
    )
  )

(deftest specifik-tests
  (testing "Specific tests"
    (test-country)
    (test-driving-licence)
    (test-isco)
    (test-language)
    (test-region)
    (test-region-norway)
    (test-sni-level)
    (test-ssyk)
    (test-sun-education-field)
    (test-sun-education-level)
    )
  )
