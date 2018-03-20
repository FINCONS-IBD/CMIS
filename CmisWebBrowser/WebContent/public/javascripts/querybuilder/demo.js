// reset builder
$('.reset').on('click', function() { 
  var target = $(this).data('target');
 
  $('#builder-'+target).queryBuilder('reset');
});

// get rules
$('.parse-json').on('click', function() {
  var target = $(this).data('target');
  var result = $('#builder-'+target).queryBuilder('getRules');
  
  if (!$.isEmptyObject(result)) {
	var result = JSON.stringify(result, null, 2);
	http_request(baseurl+'/Search'+target,result, 'post');
  }
});


function http_request(path, param, method) {
    // The rest of this code assumes you are not using a library.
    // It can be made less wordy if you use one.
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);
    
    var hiddenField = document.createElement("input");
    hiddenField.setAttribute("type", "hidden");
    hiddenField.setAttribute("name", 'filterList');
    hiddenField.setAttribute("value", param);
    
    form.appendChild(hiddenField);
    
    document.body.appendChild(form);
    form.submit();
}