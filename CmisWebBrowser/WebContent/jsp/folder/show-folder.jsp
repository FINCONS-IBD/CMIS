<%@taglib uri="/struts-tags" prefix="s"%>

<td>
<div class="span4">
	<a
		href="<s:url action="ShowFolder">
                <s:param name="folder"><s:property value="id" /></s:param>
            </s:url>">
		<img src="public/images/folder-icon-default.gif" /> <s:property
			value="name" /> </a>
</div>
</td>

<td>
<div class="span4 offset4">
</div>
</td>

<td>
<div class="span6 offset4">
	<a class="btn btn-psy"
		href="<s:url action="ShowFolderDetails">
                <s:param name="parent" value="%{folderId}"></s:param>
                <s:param name="id" value="%{id}"></s:param>
                </s:url>"><i class="icon-list-alt" style="margin-right:2px"></i><s:text
			name="Details" /> </a> &nbsp;&nbsp;
	<s:if test="%{#node.collaborator}">
		<a class="btn btn-psy"
			href="<s:url action="ShowUpdateFolder">
                <s:param name="name" value="%{name}"></s:param>
                <s:param name="parent" value="%{folderId}"></s:param>
                <s:param name="id" value="%{id}"></s:param>
                <s:param name="logic">update</s:param>
            </s:url>"><i class="icon-pencil" style="margin-right:2px"></i><s:text
				name="Update" /> </a>
                &nbsp;&nbsp; <a class="btn btn-psy"
			href="<s:url action="ShowUpdateFolder">
                <s:param name="name" value="%{name}"></s:param>
                <s:param name="parent" value="%{folderId}"></s:param>
                <s:param name="id" value="%{id}"></s:param>
                <s:param name="logic">delete</s:param>
            </s:url>"><i class="icon-trash" style="margin-right:2px"></i><s:text
				name="Delete" /> </a>
	</s:if>
<!-- 
	<s:if test="%{#node.owner}">
                &nbsp;&nbsp; <a class="btn btn-psy"
			href="<s:url action="ShowUpdateContent">
                <s:param name="name" value="%{name}"></s:param>
                <s:param name="parent" value="%{folderId}"></s:param>
                <s:param name="id" value="%{id}"></s:param>
                <s:param name="logic">permission</s:param>
            </s:url>"><s:text
				name="permission" /> </a>
	</s:if>
 -->
</div>
</td>