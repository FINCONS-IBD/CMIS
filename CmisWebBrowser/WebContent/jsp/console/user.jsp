<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<s:include value="/jsp/common/root.jsp" />

<div class="row">
	<div class="span6">
		<s:label for="name" cssClass="label" value="%{getText('user_management')}" />
		<br />
		<div id="PersonTableContainer"></div>
	</div>
</div>

<s:include value="/jsp/common/footer.jsp" />

<script type="text/javascript">
//<![CDATA[
(function($) {
	$(document).ready(function() {
		$('#PersonTableContainer').jtable({
			title : $.i18n.prop("user_list"),
			paging : true,
			pageSize: 10, //Set page size (default: 10)
			sorting : true,
			defaultSorting : "userId ASC",
			selecting : true,	
			actions : {
				listAction : 'UserAPI?logic=list',
				createAction : 'UserAPI?logic=create',
				updateAction : 'UserAPI?logic=update',
				deleteAction : 'UserAPI?logic=delete'
			},
			fields : {
				userId : {
					key : true,
					title : $.i18n.prop("id"),
					create : true,
					edit : false,
					list : true
				},
				userName : {
					title : $.i18n.prop("name"),
					width : '20%',
					edit : true
				},
				password : {
					title : $.i18n.prop("password"),
					width : '20%',
					create : true,
					edit : true,
					list : false
				},
				email : {
					title : $.i18n.prop("email"),
					width : '20%',
					edit : true
				},
				firstName : {
					title : $.i18n.prop("firstName"),
					width : '20%',
					edit : true
				},
				lastName : {
					title : $.i18n.prop("lastName"),
					width : '20%',
					edit : true
				}
			}
		});
	
		$('#PersonTableContainer').jtable('load');
	});
})(jQuery);
//]]>
</script>