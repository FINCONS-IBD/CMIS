$(document).ready(function() {
	$('#GroupTableContainer').jtable({
		title : 'グループ一覧',
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
                                    title: groupData.record.groupId + ': メンバユーザ一覧',
                                    actions: {
                                    	listAction : 'GroupAPI?logic=memberUserlist&groupId=' + groupData.record.groupId,
                            			createAction : 'GroupAPI?logic=addMember&groupId=' + groupData.record.groupId,
                            			deleteAction : 'GroupAPI?logic=removeMember&groupId=' + groupData.record.groupId
                                    },
                                    fields: {
                                    	memberId : {
                            				key : true,
                            				title : 'ID',
                            				create : true,
                            				edit : false,
                            				list : true
                            			},
                            			memberType : {
                            				title : 'タイプ',
                            				create : true,
                            				list : true,
                            				options: { 'user': 'ユーザ', 'group': 'グループ' }
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
				title : 'グループID',
				create : true,
				edit : false,
				list : true
			},
			groupName : {
				title : 'グループ名',
				width : '20%',
				list : true,
				create : true,
				edit : true
			},
			usersSize : {
				title : 'メンバユーザ数',
				width : '20%',
				list : true,
				create : false,
				edit :false
			},
			groupsSize : {
				title : 'メンバグループ数',
				width : '20%',
				list : true,
				create : false,
				edit :false,
			}
		}
	});

	$('#GroupTableContainer').jtable('load');
});