<%@taglib uri="/struts-tags" prefix="s"%>

<td>
<div class="span4">

	<a
		href="
                <s:url action='SendFile'>
                    <s:param name='node'><s:property value='id' /></s:param>
                </s:url>
            ">
		<s:push value="#{'mimetype': mimetype}">
			<s:include value="/jsp/document/show-mimetype.jsp" />
		</s:push> <s:property value="name" /> </a>
</div>
</td>

<td>
<div class="span4 offset4">
	<s:text name="size" />:&nbsp;
	<s:property value="humanReadableByteCount(size,true)" />
	<br />
	<s:text name="last_modification" />:&nbsp;
	<s:property value="convertToDate(lastModificationDate)" />
	<br />
	<s:text name="type" />:&nbsp;
	<s:property value="mimetype" />
</div>
</td>

<td>
<div class="span2 offset4">
	<a class="btn btn-psy"
		href="<s:url action="ShowDocumentDetailsSem">
                <s:param name="parent" value="%{folderId}"></s:param>
                <s:param name="id" value="%{id}"></s:param>
                </s:url>"><i class="icon-list-alt" style="margin-right:2px"></i><s:text
			name="Details" /> </a> &nbsp;&nbsp;
</div>
</td>

