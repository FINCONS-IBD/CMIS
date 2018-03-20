<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<s:include value="/jsp/common/root.jsp" />

<hr />

<!-- Update Permission Form -->
<div class="row">
	<div class="span4 offset2">
		<s:form method="POST" enctype="multipart/form-data"
			id="permission_form">
			<s:label for="name" cssClass="label" value="%{getText('name')}"></s:label>
			${name}
			<br />
			<br />
			<s:label cssClass="label" value="%{getText('permissions')}" />
			<a href="#" id="add_p" class="btn"><s:text name="add" /> </a>
			&nbsp;&nbsp;
			<a href="#" id="del_p" class="btn"><s:text name="delete" /> </a>
			<br />
			<br />
			<table id="acl_table" class="scroll"></table>
			<br />
			<a href="#" id="update_p" class="btn btn-psy"><s:text name="execute" />
			</a>
            &nbsp;&nbsp;
            <s:submit value="%{getText('cancel')}" action="ShowFolder"
				cssClass="btn" />
			<s:hidden name="parent" value="%{parent}"></s:hidden>
			<s:hidden name="id" value="%{id}"></s:hidden>
			<s:hidden name="logic" value="%{logic}"></s:hidden>
			<s:hidden name="folder" value="%{parent}"></s:hidden>
			<s:hidden name="folderPath" value="/" />
			<s:hidden id="addPermissionString" name="addPermissionString"
				value=""></s:hidden>
			<s:hidden id="delPermissionString" name="delPermissionString"
				value=""></s:hidden>
		</s:form>
		<br />

	</div>
</div>

<s:include value="/jsp/common/footer.jsp"></s:include>

<script type="text/javascript">
//<![CDATA[
(function($) {
	$(document).ready(function() {
		var lastsel2;
		$("#acl_table").jqGrid({
			datatype : "local",
			colNames : [ $.i18n.prop("user_group"), $.i18n.prop("permission")],
			colModel : [ {
				name : 'name',
				index : 'name',
				editable : true,
				edittype : "select",
				width : 150,
				editoptions : {
					value : <s:property value='%{userGroup}' escape='false' />
				}
			}, {
				name : 'permission',
				index : 'permission',
				editable : true,
				edittype : "select",
				width : 180,
				editoptions : {
					value : {"CONSUMER": $.i18n.prop("consumer"), "COLLABORATOR":$.i18n.prop("collaborator"), "OWNER":$.i18n.prop("owner")},
					dataEvents : {type: 'change', fn: function(e) { window.alert("test"); }}
				},
			}
			],
			onSelectRow : function(name) {
				if (name && name !== lastsel2) {
					$('#acl_table').jqGrid('restoreRow', lastsel2);
					$('#acl_table').jqGrid('editRow', name, true);
					lastsel2 = name;
				}
			},
			editurl : 'Dummy',
			sortname : 'name',
			sortorder : "asc",
			width : 250
		});
		var data = <s:property value="%{aclAsJson}" escape="false" />;
		for ( var i = 0; i < data.length; i++){
			//for l10n
			
			if(data[i].permission == "CONSUMER"){data[i].permission = $.i18n.prop("consumer");}
			if(data[i].permission == "COLLABORATOR"){data[i].permission = $.i18n.prop("collaborator");}	
			if(data[i].permission == "OWNER"){data[i].permission = $.i18n.prop("owner");}
				
			$("#acl_table").jqGrid('addRowData', data[i].name, data[i]);
		}
			
		$("#add_p").click(function() {
			$("#acl_table").jqGrid('editGridRow', "new", {
				reloadAfterSubmit : false,
				closeOnEscape : true,
				closeAfterAdd : true,
				bSubmit : $.i18n.prop("_add"),
				bCancel : $.i18n.prop("_cancel"),
				height: 200,
				width: 500
			});
		});
		$("#del_p").click(function() {
			var gr = jQuery("#acl_table").jqGrid('getGridParam', 'selrow');
			if (gr != null)
				$("#acl_table").jqGrid('delGridRow', gr, {
					reloadAfterSubmit : false,
					msg : $.i18n.prop("delete_confirm"),
					closeOnEscape : true,
					caption : $.i18n.prop("_delete"),
					bSubmit : $.i18n.prop("_delete"),
					bCancel : $.i18n.prop("_cancel"),
					height: 200,
	                width: 500,
					onclickSubmit : function() {
						var selrow = jQuery("#acl_table").jqGrid('getLocalRow', gr);

						//overrite selrow.permission value for i18n
						var delPermission;
			            if(selrow.permission == $.i18n.prop("consumer") || selrow.permission == "CONSUMER"){
			            	delPermission = "CONSUMER";
			            }else if(selrow.permission == $.i18n.prop("collaborator") || selrow.permission == "COLLABORATOR"){
			            	delPermission = "COLLABORATOR";
			            }else if(selrow.permission == $.i18n.prop("owner") || selrow.permission == "OWNER"){
			            	delPermission = "OWNER";
			            }
						
						$("#delPermissionString").val($("#delPermissionString").val() + selrow.name + ";" + delPermission + ";;");	//for i18n
					}
				});
			else
				alert("Please Select Row to delete!");
		});
		$("#update_p").click(function() {
			var addPermissionString = "";
			var rowIds = $("#acl_table").jqGrid('getDataIDs');
            for (var i in rowIds) {
            	var row = jQuery("#acl_table").jqGrid('getLocalRow', rowIds[i]);
                addPermissionString += row.name;
                addPermissionString += ";";
                
                //translate back permission value for i18n
                if(row.permission == $.i18n.prop("consumer") || row.permission == "CONSUMER"){
                	addPermissionString += "CONSUMER";
                }else if(row.permission == $.i18n.prop("collaborator") || row.permission == "COLLABORATOR"){
                	addPermissionString += "COLLABORATOR";
                }else if(row.permission == $.i18n.prop("owner") || row.permission == "OWNER"){
                	addPermissionString += "OWNER";
                }
                
                addPermissionString += ";;";
            }			
			$("#addPermissionString").val(addPermissionString);
			$("#permission_form").get(0).setAttribute('action', 'UpdatePermission');
			$("#permission_form").submit();
		});
	});
})(jQuery);
//]]>
</script>
