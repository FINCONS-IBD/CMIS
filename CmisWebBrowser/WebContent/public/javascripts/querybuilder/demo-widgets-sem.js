// Fix for Selectize
$('#builder-widgets-sem').on('afterCreateRuleInput.queryBuilder', function(e, rule) {
  if (rule.filter.plugin == 'selectize') {
//	rule.$el.find('.item').css('padding-right','20px');
    rule.$el.find('.rule-value-container').css('width', '250px')
      .find('.selectize-control').removeClass('form-control');
  }
});

$('#builder-widgets-sem').queryBuilder({
  plugins: ['bt-tooltip-errors'],
  conditions:['AND'],
  allow_groups: false,
  operators: [
	    { type: 'equal'},
	    { type: 'not_equal'},
		{ type: 'talk_about', nb_inputs: 1, multiple: false, apply_to: ['string'] },
		{ type: 'not_talk_about', nb_inputs: 1, multiple: false, apply_to: ['string'] }
  ],
  filters: [
  {
	  id: 'containsText',
	  label: 'contains entity',
	  type: 'string',
	  validation:{
		  format: '^([A-Za-z0-9]+[_ ]?[A-Za-z0-9]+)+$'
	  },
	  operators: ['equal', 'not_equal']
  },
/*  
  {
	  id: 'containsConcept',
	  label: 'contains concept',
	  type: 'string',
	  validation:{
		  format: '^[A-Za-z0-9_]*[A-Za-z0-9][A-Za-z0-9_]*$'
	  },
	  operators: ['equal', 'not_equal']
  },
*/
  {
	  id: 'title',
	  label: 'doc title',
	  type: 'string',	  
	  validation:{
		  format: '^([A-Za-z0-9]+[_ ]?[A-Za-z0-9]+)+$'
	  },
	  operators: ['equal', 'not_equal']  
  },
  {
	  id: 'creator',
	  label: 'author',
	  type: 'string',  
	  validation:{
		  format: '^([A-Za-z0-9]+[_ ]?[A-Za-z0-9]+)+$'
	  },
	  operators: ['equal', 'not_equal']  
  },
  {
	  id: 'topic',
	  label: 'topic',
	  type: 'string',
	  validation:{
		  format: '^([A-Za-z0-9]+[_ ]?[A-Za-z0-9]+)+$'
	  },    
	  operators: ['talk_about', 'not_talk_about']
  },
  {
	  	id: 'containsConcept',
	  	label: 'contains concept',
	    type: 'string',
	    plugin: 'selectize',
	    plugin_config: {
	        valueField: 'conceptURI',
	        labelField: 'conceptLabel',
	        searchField: 'conceptLabel',
	        sortField: 'conceptURI',
	        size: 250,
	        create: true,
	        maxItems: 1,
	        onInitialize: function() {
	        	$.blockUI({ message: '<h4><img src="public/images/busy.gif" /> Just a moment...</h4>' });

	            var that = this;
	            var url = "https://virtuoso-ibd.finconsgroup.com:4433/sparql";
	            var query = "SELECT ?concept ?label WHERE { GRAPH <http://89.207.106.75:8890/conceptsGraph> {?concept ?p ?label}}";
	            
	            var queryUrl = encodeURI( url+"?query="+query+"&format=json" );
	            $.ajax({
	                dataType: "jsonp",  
	                url: queryUrl,
	                success: function( _data ) {
	                	$.unblockUI();
	                    var results = _data.results.bindings;
	                    for ( var i in results ) {
	                    	var item = {};

	                        var res = results[i];
	                    	item.id=i;
	                    	item.conceptURI=res.concept.value;
	                    	item.conceptLabel=res.label.value;
	                    	
	                    	that.addOption(item);
	                        //console.log(res.Concept.value);
	                    }
	                }
	            });


	            /*
	            $.getJSON('http://localhost:3000/posts', function(data) {
	                data.forEach(function(item) {
	                  that.addOption(item);
	                });
	              });
	            }
	            */
	       },
	       valueSetter: function(rule, value) {
	    	      rule.$el.find('.rule-value-container input')[0].selectize.setValue(value);
	       }
	    }
  }
  ]
});