component {
	// Elvis / null-coalesce on member access (qhapdc shape)
	function test(required struct pat) {
		return pat.uniqueno ?: '';
	}
}
