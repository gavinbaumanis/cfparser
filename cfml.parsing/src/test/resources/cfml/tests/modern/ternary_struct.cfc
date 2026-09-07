component {
	// Ternary that yields a struct (waitlist shape)
	function test(any x) {
		return isNull(x) ? '' : { value = x, html = '<b>#x#</b>' };
	}
}
