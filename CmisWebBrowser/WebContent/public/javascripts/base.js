(function($) {
	function loadBundles(lang) {
		jQuery.i18n.properties({
			name : 'messages',
			path : 'public/javascripts/properties/',
			mode : 'both',
			language : lang
		});
	}
	$(document).ready(function() {
		loadBundles("ja");
	});
})(jQuery);