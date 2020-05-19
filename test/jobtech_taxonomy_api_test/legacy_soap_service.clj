(ns jobtech-taxonomy-api-test.legacy-soap-service
  (:require [clojure.test :refer :all]
            [jobtech-taxonomy-api-test.core :refer :all]
            [clj-http.client :as client]
            [paos.service :as service]
            [paos.wsdl :as wsdl]
            [cheshire.core :refer :all])
  )

(def taxonomy-service-url "http://api.arbetsformedlingen.se/taxonomi/v0/TaxonomiService.asmx?wsdl")

(defn parse-response [{:keys [status body] :as response} body-parser fail-parser]
  (assoc response
         :body
         (case status
           200 (body-parser body)
           500 (fail-parser body))))

(defn get-guide-tree []
  (let [
        soap-service (wsdl/parse taxonomy-service-url)
        srv          (get-in taxwsdl ["TaxonomiServiceSoap12" :operations  "GetSUNGuideTree" ])
        soap-url     "http://api.arbetsformedlingen.se/taxonomi/v0/TaxonomiService.asmx"
        content-type (service/content-type srv)
        headers      (service/soap-headers srv)
        mapping      (service/request-mapping srv)
        context      (assoc-in mapping ["Envelope" "Body" "GetSUNGuideTree" "languageId" :__value] "502")
        body         (service/wrap-body srv context)
        parse-fn     (partial service/parse-response srv)
        ]

    (-> soap-url
        (client/post {:content-type content-type
                      :body         body
                      :headers      headers})
        :body
        parse-fn)
    )
  )
