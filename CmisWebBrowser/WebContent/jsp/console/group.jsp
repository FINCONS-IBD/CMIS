<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<s:include value="/jsp/common/root.jsp" />

<div class="row">
	<div class="span6">
		<s:label for="name" cssClass="label" value="%{getText('group_management')}" />
		<br />
		<div id="GroupTableContainer"></div>
	</div>
</div>

<s:include value="/jsp/common/footer.jsp" />

<script type="text/javascript">
//<![CDATA[
(function($) {
	$(document).ready(function() {
		$('#GroupTableContainer').jtable({
			title : $.i18n.prop("group_list"),
			sorting : true,
			selecting : true,	
				
			actions : {
				listAction : 'GroupAPI?logic=list',
				createAction : 'GroupAPI?logic=create',
				updateAction : 'GroupAPI?logic=update',
				deleteAction : 'GroupAPI?logic=delete'
			},
			fields : {
				memberUsers : {
					title : '',
					width : '5%',
					sorting : false,
					edit : false,
					create : false,
					//Define child table for group members
					display: function (groupData) {
	                    //Create an image that will be used to open child table
	                    var $img = $('<img src="public/javascripts/jtable/themes/lightcolor/edit.png" title="Edit phone numbers" />');
	                    //Open child table when user clicks the image
	                    $img.click(function () {
	                        $('#GroupTableContainer').jtable('openChildTable',
	                                $img.closest('tr'),
	                                {
	                                    title: groupData.record.groupId + ': ' + $.i18n.prop("member_list"),
	                                    sorting : true,
	                                    defaultSorting : "memberId ASC",
	                                    actions: {
	                                    	listAction : 'GroupAPI?logic=memberUserlist&groupId=' + groupData.record.groupId,
	                            			createAction : 'GroupAPI?logic=addMember&groupId=' + groupData.record.groupId,
	                            			deleteAction : 'GroupAPI?logic=removeMember&groupId=' + groupData.record.groupId
	                                    },
	                                    fields: {
	                                    	memberId : {
	                            				key : true,
	                            				title : $.i18n.prop("id"),
	                            				create : true,
	                            				edit : false,
	                            				list : true
	                            			},
	                            			memberType : {
	                            				title : $.i18n.prop("type"),
	                            				create : true,
	                            				list : true,
	                            				options: { 'user': $.i18n.prop("user"), 'group': $.i18n.prop("group")}
	                            			}
	                                    },
	                                  	//reload page when a member is successfully added
	                                  	recordAdded : function(){
	                                  	document.location.reload(true);
	                                    }, 
	                                  	//reload page when a member is successfully removed
	                                  	recordDeleted : function(){
	                                	document.location.reload(true);
	                                  } 
	                                }, function (data) { //opened handler
	                                    data.childTable.jtable('load');
	                                });
	                    });
	                    //Return image to show on the person row
	                    return $img;
	                },
				},
				groupId : {
					key : true,
					title : $.i18n.prop("id"),
					create : true,
					edit : false,
					list : true
				},
				groupName : {
					title : $.i18n.prop("name"),
					width : '20%',
					list : true,
					create : true,
					edit : true
				},
				usersSize : {
					title : $.i18n.prop("number_users"),
					width : '20%',
					list : true,
					create : false,
					edit :false
				},
				groupsSize : {
					title : $.i18n.prop("number_groups"),
					width : '20%',
					list : true,
					create : false,
					edit :false,
				}
			}
		});

		$('#GroupTableContainer').jtable('load');
	});
})(jQuery);
//]]>
</script>