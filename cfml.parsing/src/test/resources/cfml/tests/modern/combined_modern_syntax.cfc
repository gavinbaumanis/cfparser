component {
	function test() {
		rows = [];
		rptdetails = {
			'heading' = 'iPOS',
			'data' = seq.Map(rows, (row) => {
				return [ row.id ];
			})
		};
		x = pat.uniqueno ?: '';
		out = isNull(x) ? '' : { value = x, html = 'y' };
		s = '#DateFormat(ts,'YYYYMMDD')#';
		q = QueryExecute("SELECT '##' AS mrn FROM dual");
		filtered = rows.Filter((row) => {
			loop collection = row.subscores index = "k" item = "v" {
				if (v.score > 2) {
					return true;
				}
			}
			return false;
		});
	}

	function declOk(required boolean flag, required string name,) {
		return CompName::runStatic(true, 'x',);
	}
}
