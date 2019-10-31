(ns jobtech-taxonomy-api-test.smoke-test
  (:require [clojure.test :refer :all]
            [jobtech-taxonomy-api-test.core :refer :all]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            ))


;; http://jobtech-taxonomy-api-spec-jobtech-taxonomy-api.test.services.jtech.se/v1/taxonomy/swagger-ui/index.html#!/Main/get_v1_taxonomy_main_concept_types



;;(def api-key (System/getenv "JOBTECH_TAXONOMY_API_KEY"))
;;(def base-url (System/getenv "JOBTECH_TAXONOMY_API_URL") )

(def api-key "111")
(def base-url "http://jobtech-taxonomy-api-spec-jobtech-taxonomy-api.test.services.jtech.se/")
(def main-base-url (str base-url "v1/taxonomy/main/"))
(def specific-base-url (str base-url "v1/taxonomy/specific/concepts/"))


(defn call-api
  ([url query-params]
;;   (println url)
;;   (println query-params)
   (:body (client/get url
                      (cond->
                          {:as :json-strict
                           :headers {"api-key" api-key}
                           ;; :debug true
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


(defn call-concept-types []
  (call-api (str main-base-url "concept/types"))
  )

(defn test-concept-types []
  (is (contains?  (set (call-concept-types)) "skill"))
  )

(deftest test-concept-types-test
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

(deftest test-concept-animinering-test
  (test-concepts-animering)
  )


(defn call-changes []
  (call-api-main "changes" {"from-version" 1 "limit" 1})
  )

(defn test-changes []
  (is (= 2 (:taxonomy/version (first (call-changes)))))
  )

(deftest test-changes-test
  (call-changes)
  )



(defn call-relation-types []
  (call-api-main "relation/types")
  )

(defn test-relation-types []
  (is (contains? (set (call-relation-types))  "broader"))
  )

(defn call-replaced-by-changes []
  (call-api-main "replaced-by-changes" {"from-version" 1})
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


(defn test-call-driving-licence []
  (is (contains?  (set (:taxonomy/implicit-driving-licences (first (call-driving-licence))))  #:taxonomy{:id "4HpY_e2U_TUH", :driving-licence-code-2013 "AM"} ))
  )



(defn call-employment-duration []
  (call-api-specific "employment-duration" {"eures-code-2014" "TF" "limit" 1})
  )

(defn test-employment-duration []
  (is (= "TF" (:taxonomy/eures-code-2014 (first (call-employment-duration)))))
  )
