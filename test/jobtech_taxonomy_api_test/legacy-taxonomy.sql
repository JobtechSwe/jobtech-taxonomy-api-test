SELECT term
FROM TaxonomyDB.dbo.MunicipalityTerm
WHERE languageID = 502
ORDER BY term ASC



---

SELECT term
FROM TaxonomyDB.dbo.OccupationNameTerm
WHERE languageID = 502
ORDER BY term ASC

--

SELECT term
FROM TaxonomyDB.dbo.PopularSynonym
ORDER BY term ASC

--

SELECT term
FROM TaxonomyDB.dbo.SkillHeadlineTerm
WHERE languageID = 502
ORDER BY term ASC
