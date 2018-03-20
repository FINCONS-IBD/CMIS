<%@taglib uri="/struts-tags" prefix="s"%>
<%@taglib uri="/struts-dojo-tags" prefix="sx"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<sx:head />
	</head>
        
     
    <link rel="stylesheet" type="text/css" href="public/stylesheets/bootstrap-combined.min.css"></link>
       
    <link href="public/stylesheets/bootstrap_3.3.1.css" rel="stylesheet">
    
    <link href="public/stylesheets/querybuilder/docs.min.css" rel="stylesheet">
	<link href="public/stylesheets/querybuilder/style.css" rel="stylesheet">
    
    <!-- CDN javascript -->
    <!--
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    -->
    <script src="public/javascripts/jquery/jquery-1.11.2.min.js"></script>
	<script src="public/javascripts/bootstrap.min.js"></script>
	<!--<script src="public/javascripts/bootbox.min.js"></script>-->
	<script src="public/javascripts/querybuilder/docs.min.js"></script>
    	
   <!-- <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.24/jquery-ui.min.js"></script> -->
    
    <script type="text/javascript" src="public/javascripts/jquery.jstree.js"></script>
    
    
    <!-- local css & javascript -->
    
    <link rel="stylesheet" type="text/css" href="public/stylesheets/jquery-ui-1.8.21.custom.css" />
    
    <link rel="stylesheet" type="text/css" href="public/stylesheets/ui.jqgrid.css" />
    <link rel="stylesheet" type="text/css" href="public/stylesheets/base.css" />
    <script type="text/javascript" src="public/javascripts/grid.locale-en.js" ></script>
    <script type="text/javascript" src="public/javascripts/grid.locale-ja.js"></script>
    <script type="text/javascript" src="public/javascripts/jquery.jqGrid.min.js"></script>
    <script type="text/javascript" src="public/javascripts/jquery.i18n.properties-1.0.9.js" ></script>
    <script type="text/javascript" src="public/javascripts/base.js"></script>
    <script type="text/javascript" src="public/javascripts/bootstrap-dropdown.js"></script>
        
    <!-- Include one of jTable styles. -->
	<link href="public/javascripts/jtable/themes/standard/blue/jtable_blue.css" rel="stylesheet" type="text/css" />
 
	<!-- Include jTable script file.
	<script src="public/javascripts/jtable/jquery.jtable.min.js" type="text/javascript"></script> -->
    <link rel="shortcut icon" type="image/png" href="public/images/icon_small.ico" />
    
    <title>Cmis Knowledge Browser</title>
    <script type="text/javascript">
    
    var filterList = [];
        
    (function($) {
        $(document).ready(function() {
            $('.dropdown-toggle').dropdown();
            
            var hideThis = document.getElementById('dateFieldDiv');
            if(hideThis != null)
            	hideThis.style.display = 'none';
            	
            chechFilterList();
        });
    })(jQuery);
    
	function chechFilterList() {
		if(filterList.length ==0)
			$('#ShowFolder_Search').prop("disabled",true);
		else 
			$('#ShowFolder_Search').prop("disabled",false);
		
	}

    function addToFilter () {
    	var selection = document.getElementById('resultAction_yourSearchEngine').value;
    	if(selection.indexOf('Date') > -1){
			var addFood = selection+'='+document.getElementsByName('dateField')[0].value+'';
    	}else
    		var addFood = selection+'='+document.getElementById('addFilterText').value+'';
    	
        filterList.push(addFood);

        document.getElementById('filterList').value = filterList;
        
        for (i = 0; i < filterList.length; i++)   {
            var newFood = filterList[i] + " <i class='icon-remove' onClick='removeRecord(" + i + ");'></i><br>";
        };
        document.getElementById('filters').innerHTML += newFood;
        
        chechFilterList();
    }
    
    function processChangeFilter(value){
        var hideDate = document.getElementById('dateFieldDiv');
        var hideText = document.getElementById('addFilterText');
        
		if(value.indexOf('Date') > -1){
            hideDate.style.display = 'block';    
            $('#addFilterText').hide();
		}
		else{
            hideDate.style.display = 'none';    
            $('#addFilterText').show();
		}
    }
    
    function removeRecord (i) {
        filterList.splice(i, 1); // remove element at position i
        document.getElementById('filterList').value = filterList;
        
        var newFood = "";
        for (var i = 0; i < filterList.length; i++) {
            newFood += filterList[i] + " <a href='#' onClick='removeRecord(" + i + ");'><i class='icon-remove'></i></a><br>";
        };
        document.getElementById('filters').innerHTML = newFood;
        
        chechFilterList();
    }
    </script>
</head>

<body>
<div id="navbar" class="navbar navbar-fixed-top" data-scrollspy="scrollspy">
   	<div style="width: 20%; float: left;">
   		<img src="public/images/logo-psy.png" id="logo_psy" />
   	</div>
       <div class="container" style="width: 60%; float: left; text-align: center">
           <ul class="nav" style="width: 100%;">
           	<img src="public/images/banner_2.png" style="max-width: 100%; margin:15px 5px 0px 15px"/><!-- <h2>CMIS Knowledge Browser</h2>-->
               <s:if test="%{#session['user'] == 'admin'}">
                   <li class="dropdown">
                       <a href="#" class="dropdown-toggle" data-toggle="dropdown"><s:property value="%{getText('console')}" /></a>
                       <ul class="dropdown-menu">
                           <li class="divider"></li>
                           <li><a href="<s:url action="ShowUser"/>"><s:property value="%{getText('console_user')}" /></a></li>
                       	<li><a href="<s:url action="ShowGroup"/>"><s:property value="%{getText('console_group')}" /></a></li>
                       </ul>
                   </li>
               </s:if>
               <s:if test="%{#session['logged-in'] == 'true'}">
                   <p style="text-align:right;"><b>Welcome</b> <s:property value="#session['user']"/> <a href="<s:url action="Logout"/>">Logout</a></p>
               </s:if>
           </ul>
       </div>
       <div style="width: 20%; float: left;">
		<img src="public/images/logoFincons.png" id="logo_fincons" />
	</div>
</div>

<hr />
<br />

<!-- end tags are in footer.jsp --> 
<div class="container" style="margin-top: 5%; margin-bottom: 3%">

