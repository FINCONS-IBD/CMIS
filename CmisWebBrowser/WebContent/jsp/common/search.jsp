<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>

<s:if test="%{#session['logged-in'] == 'true'}">

<div class="row" style="margin-top: 40px;">
	<div class="span8">
		<h4>CMIS Query Editor</h4>

		<div>
			<div id="builder-widgets"></div>
			<div class="btn-group">
			  <button class="btn btn-danger reset" data-target="widgets">Reset</button>
			  <button class="btn btn-primary parse-json" data-target="widgets">Search</button>
			</div>
		</div>
		
		<link href="https://querybuilder.js.org/dist/bootstrap-datepicker/dist/css/bootstrap-datepicker3.min.css" rel="stylesheet">
		<link href="https://querybuilder.js.org/dist/seiyria-bootstrap-slider/dist/css/bootstrap-slider.min.css" rel="stylesheet">
		<link href="https://querybuilder.js.org/dist/selectize/dist/css/selectize.bootstrap3.css" rel="stylesheet">
		<link href="https://querybuilder.js.org/dist/bootstrap-select/dist/css/bootstrap-select.min.css" rel="stylesheet">
		<link href="https://querybuilder.js.org/dist/awesome-bootstrap-checkbox/awesome-bootstrap-checkbox.css" rel="stylesheet">
		<link href="https://querybuilder.js.org/dist/jQuery-QueryBuilder/dist/css/query-builder.default.min.css" rel="stylesheet">

		<script src="https://querybuilder.js.org/dist/moment/min/moment.min.js"></script>
		<script src="https://querybuilder.js.org/dist/bootstrap-datepicker/dist/js/bootstrap-datepicker.min.js"></script>
		<script src="https://querybuilder.js.org/dist/seiyria-bootstrap-slider/dist/bootstrap-slider.min.js"></script>
		<script src="https://querybuilder.js.org/dist/selectize/dist/js/standalone/selectize.min.js"></script>
		<script src="https://querybuilder.js.org/dist/bootstrap-select/dist/js/bootstrap-select.min.js"></script>
		<script src="https://querybuilder.js.org/dist/jQuery-QueryBuilder/dist/js/query-builder.standalone.min.js"></script>
		<script src="https://querybuilder.js.org/dist/sql-parser/browser/sql-parser.js"></script>

		<script>var baseurl = 'https://'+window.location.host+'/CmisWebBrowser';</script>
		
		<script src="public/javascripts/querybuilder/demo-widgets.js"></script>

    </div>
</div>    
</s:if>
