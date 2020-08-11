(ns jobtech-taxonomy-api-test.config)

(def api-key
  (if (System/getenv "JOBTECH_TAXONOMY_API_KEY")
    (System/getenv "JOBTECH_TAXONOMY_API_KEY")
    "111"
    )
  )

(def base-url
  (if (System/getenv "JOBTECH_TAXONOMY_API_URL")
    (System/getenv "JOBTECH_TAXONOMY_API_URL")
    "http://localhost:3000/"
    )
  )
