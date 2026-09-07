<!--- CFM script block covering the same modern constructs --->
<cfscript>
rows = [];
rptdetails = {
	'data' = seq.Map(rows, (row) => {
		return row.id;
	})
};
x = pat.uniqueno ?: '';
out = isNull(x) ? '' : { value = x, html = 'y' };
s = '#DateFormat(ts,'YYYYMMDD')#';
q = QueryExecute("SELECT '##' AS mrn");
filtered = rows.Filter((row) => {
	loop collection = row.subscores index = "k" item = "v" {
		if (v.score > 2) {
			return true;
		}
	}
	return false;
});
ok = CompName::runStatic(true, 'x',);
</cfscript>
