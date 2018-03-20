// Fix for Selectize
$('#builder-widgets').on('afterCreateRuleInput.queryBuilder', function(e, rule) {
  if (rule.filter.plugin == 'selectize') {
    rule.$el.find('.rule-value-container').css('min-width', '200px')
      .find('.selectize-control').removeClass('form-control');
  }
});

$('#builder-widgets').queryBuilder({
  plugins: ['bt-tooltip-errors'],
  conditions:['AND'],
  allow_groups: false,
  filters: [
/*    {
    id: 'date',
    label: 'Datepicker',
    type: 'date',
    validation: {
      format: 'YYYY/MM/DD'
    },
    plugin: 'datepicker',
    plugin_config: {
      format: 'yyyy/mm/dd',
      todayBtn: 'linked',
      todayHighlight: true,
      autoclose: true
    }
  }, {
    id: 'rate',
    label: 'Slider',
    type: 'integer',
    validation: {
      min: 0,
      max: 100
    },
    plugin: 'slider',
    plugin_config: {
      min: 0,
      max: 100,
      value: 0
    },
    valueSetter: function(rule, value) {
      if (rule.operator.nb_inputs == 1) value = [value];
      rule.$el.find('.rule-value-container input').each(function(i) {
        $(this).slider('setValue', value[i] || 0);
      });
    },
    valueGetter: function(rule) {
      var value = [];
      rule.$el.find('.rule-value-container input').each(function() {
        value.push($(this).slider('getValue'));
      });
      return rule.operator.nb_inputs == 1 ? value[0] : value;
    }
  }, {
    id: 'category',
    label: 'Selectize',
    type: 'string',
    plugin: 'selectize',
    plugin_config: {
      valueField: 'id',
      labelField: 'name',
      searchField: 'name',
      sortField: 'name',
      create: true,
      maxItems: 1,
      plugins: ['remove_button'],
      onInitialize: function() {
        var that = this;
        if (localStorage.demoData === undefined) {
          alert('SI!');
          $.getJSON(baseurl + '/public/data/demo-data.json', function(data) {
            localStorage.demoData = JSON.stringify(data);
            data.forEach(function(item) {
              that.addOption(item);
            });
          });
        }
        else {
          JSON.parse(localStorage.demoData).forEach(function(item) {
            that.addOption(item);
          });
        }
      }
    },
    valueSetter: function(rule, value) {
      rule.$el.find('.rule-value-container input')[0].selectize.setValue(value);
    }
  }, {
    id: 'coord',
    label: 'Coordinates',
    type: 'string',
    validation: {
      format: /^[A-C]{1}.[1-6]{1}$/
    },
    input: function(rule, name) {
      var $container = rule.$el.find('.rule-value-container');
      
      $container.on('change', '[name='+ name +'_1]', function(){
        var h = '';
        
        switch ($(this).val()) {
          case 'A':
            h = '<option value="-1">-</option> <option value="1">1</option> <option value="2">2</option>';
            break;
          case 'B':
            h = '<option value="-1">-</option> <option value="3">3</option> <option value="4">4</option>';
            break;
          case 'C':
            h = '<option value="-1">-</option> <option value="5">5</option> <option value="6">6</option>';
            break;
        }
        
        $container.find('[name='+ name +'_2]').html(h).toggle(h!='');
      });
      
      return '\
      <select name="'+ name +'_1"> \
        <option value="-1">-</option> \
        <option value="A">A</option> \
        <option value="B">B</option> \
        <option value="C">C</option> \
      </select> \
      <select name="'+ name +'_2" style="display:none;"></select>';
    },
    valueGetter: function(rule) {
      return rule.$el.find('.rule-value-container [name$=_1]').val()
        +'.'+ rule.$el.find('.rule-value-container [name$=_2]').val();
    },
    valueSetter: function(rule, value) {
      if (rule.operator.nb_inputs > 0) {
        var val = value.split('.');
        
        rule.$el.find('.rule-value-container [name$=_1]').val(val[0]).trigger('change');
        rule.$el.find('.rule-value-container [name$=_2]').val(val[1]).trigger('change');
      }
    }
  },*/
  {
	  id: 'createdBy',
	  label: 'CreatedBy',
	  type: 'string',
	  validation:{
		  format: '^[A-Za-z0-9_]*[A-Za-z0-9][A-Za-z0-9_]*$'
	  },
	  operators: ['equal', 'not_equal']
  },
  {
	  id: 'lastModifiedBy',
	  label: 'LastModifiedBy',
	  type: 'string',
	  validation:{
		  format: '^[A-Za-z0-9_]*[A-Za-z0-9][A-Za-z0-9_]*$'
	  },
	  operators: ['equal', 'not_equal']
  },
  {
	  id: 'creationDate',
	  label: 'CreationDate',
	  type: 'date',
	  validation: {
	  format: 'YYYY-MM-DD'
	  },
	  plugin: 'datepicker',
	  plugin_config: {
	    format: 'yyyy-mm-dd',
	    todayBtn: 'linked',
	    todayHighlight: true,
	    autoclose: true,
	    forceParse:true,
	    orientation:"bottom"
	  },
	  operators: ['equal', 'not_equal', 'less', 'less_or_equal', 'greater', 'greater_or_equal', 'between']	  
  },
  {
	  id: 'lastModificationDate',
	  label: 'LastModificationDate',
	  type: 'date',
	  validation: {
	  format: 'YYYY-MM-DD'
	  },
	  plugin: 'datepicker',
	  plugin_config: {
	    format: 'yyyy-mm-dd',
	    todayBtn: 'linked',
	    todayHighlight: true,
	    autoclose: true,
	    forceParse:true,
	    orientation:"bottom"
	  },
	  operators: ['equal', 'not_equal', 'less', 'less_or_equal', 'greater', 'greater_or_equal', 'between']	  
  },
  {
	  id: 'contains',
	  label: 'Contains',
	  type: 'string',
	  operators: ['equal', 'not_equal']
  }  
  ]
});