(function($) {
	function handleResponse(data) {
		// TODO show aspect select box
		console.log(data);
	}
	$("document").ready(function() {
		var opts = {
				type: "GET",
				url: $("input:hidden#aspects-base-url").val(),
				success: handleResponse
		};
		$.ajax(opts);
	});
})(jQuery);