component {
	// Tag-in-script loop collection ... { } inside an arrow (iposhighscore)
	function test(query rptdata) {
		return rptdata.Filter((row) => {
			var score = { subscores = {} };
			loop
				collection = score.subscores
				index = "k"
				item = "value" {
					if (value.score > 2 && value.completed) {
						return true;
					}
				}
			return false;
		});
	}
}
