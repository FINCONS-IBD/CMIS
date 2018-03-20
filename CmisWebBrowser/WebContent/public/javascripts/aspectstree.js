(function($) {
	$(document).ready(function() {
        
        $("p.treeName").click(function () {
        	id = $(this).attr("id");
        	$("div#" + id).toggle();
        });
    });
	
	
})(jQuery);