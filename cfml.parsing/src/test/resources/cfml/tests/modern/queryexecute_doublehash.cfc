component {
	// ## inside a double-quoted QueryExecute SQL string
	function test(required numeric id) {
		return QueryExecute("
			SELECT mrn || COALESCE('##' || NULLIF(readmitcount, 1), '') AS mrn
			FROM patientinfo
			WHERE id = :id
		", {
			'id' = { cfsqltype = "cf_sql_integer", value = id }
		});
	}
}
