(ns jobtech-taxonomy-api-test.legacy-soap-service
  (:require [clojure.test :refer :all]
            [jobtech-taxonomy-api-test.core :refer :all]
            [clj-http.client :as client]
            [paos.service :as service]
            [paos.wsdl :as wsdl]
            [cheshire.core :refer :all]))

(def taxonomy-service-url "http://api.arbetsformedlingen.se/taxonomi/v0/TaxonomiService.asmx?wsdl")

(defn parse-response [{:keys [status body] :as response} body-parser fail-parser]
  (assoc response
         :body
         (case status
           200 (body-parser body)
           500 (fail-parser body))))


(defn parse-field-3-leaf [field-3-leaf]
  (let [sun-field-3-fun #(get-in field-3-leaf ["SUNField3" % "__value"]) ]
    {:taxonomy/deprecated-legacy-id (sun-field-3-fun "SUNField3ID")
     :taxonomy/preferred-label (sun-field-3-fun "Term")
     :taxonomy/sun-education-level-code-2000 (sun-field-3-fun "SUNField3Code")
     }
    )
  )

(defn parse-guide-leaf [leaf]
  (let [sun-field-2-fun #(get-in leaf  ["SUNGuideLeaf" "SUNField2" % "__value"])]
    {:taxonomy/deprecated-legacy-id (sun-field-2-fun "SUNField2ID")
     :taxonomy/preferred-label (sun-field-2-fun "Term")
     :taxonomy/sun-education-level-code-2000 (sun-field-2-fun "SUNField2Code")
     :related (map parse-field-3-leaf (get-in leaf ["SUNGuideLeaf"  "SUNField3List" "SUNField3s"  ]))
     }
    )
  )

(defn parse-guide-branch [node]
  (let [sun-level-fun #(get-in node ["SUNGuideBranch" "SUNLevel1"  % "__value"])

        ]
    {:taxonomy/deprecated-legacy-id (sun-level-fun "SUNLevel1ID")
     :taxonomy/preferred-label (sun-level-fun "Term")
     :taxonomy/sun-education-level-code-2000 (sun-level-fun "SUNLevel1Code")
     :related (map parse-guide-leaf (get-in node ["SUNGuideBranch" "SUNGuideLeaves" "SUNGuideLeafs"  ]))
     }))

(defn get-guide-tree []
  (let [soap-service (wsdl/parse taxonomy-service-url)
        srv          (get-in taxwsdl ["TaxonomiServiceSoap12" :operations  "GetSUNGuideTree"])
        soap-url     "http://api.arbetsformedlingen.se/taxonomi/v0/TaxonomiService.asmx"
        content-type (service/content-type srv)
        headers      (service/soap-headers srv)
        mapping      (service/request-mapping srv)
        context      (assoc-in mapping ["Envelope" "Body" "GetSUNGuideTree" "languageId" :__value] "502")
        body         (service/wrap-body srv context)
        parse-fn     (partial service/parse-response srv)]

    (-> soap-url
        (client/post {:content-type content-type
                      :body         body
                      :headers      headers})
        :body
        parse-fn
        (get-in ["Envelope" "Body" "GetSUNGuideTreeResponse" "GetSUNGuideTreeResult" "SUNGuideBranches"]))))
