$(document).ready(function() {
	$('#PersonTableContainer').jtable({
		title : 'ユーザ管理',
		sorting : true,
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
				title : 'ID',
				create : true,
				edit : false,
				list : true
			},
			userName : {
				title : 'Name',
				width : '20%',
				edit : true
			},
			password : {
				title : 'password',
				width : '20%',
				create : true,
				edit : true,
				list : false
			},
			email : {
				title : 'email',
				width : '20%',
				edit : true
			},
			firstName : {
				title : 'firstName',
				width : '20%',
				edit : true
			},
			lastName : {
				title : 'lastName',
				width : '20%',
				edit : true
			}
		}
	});

	$('#PersonTableContainer').jtable('load');
});