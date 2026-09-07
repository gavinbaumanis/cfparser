component {
	// Block arrow as struct value (iposhighscore shape)
	function test() {
		rows = [];
		rptdetails = {
			'heading' = 'iPOS',
			'data' = seq.Map(rows, (row) => {
				return [ row.id, row.name ];
			})
		};
		return rptdetails;
	}
}
