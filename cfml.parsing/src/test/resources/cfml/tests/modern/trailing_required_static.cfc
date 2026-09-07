component {
	// Trailing commas in decl + call args, required boolean, :: static call
	function declOk(required boolean flag, required string name,) {
		return true;
	}

	function callTrailing() {
		return declOk(true, 'x',);
	}

	function staticCall() {
		return CompName::runStatic();
	}
}
