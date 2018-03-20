<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%--set current folder --%>
<s:set var="folderId" value="%{folder}" />

<s:iterator value="setBreadcrumb()" var="crumb" status="st">
	<a
		href="<s:url action="ShowFolder"><s:param name="folder"><s:property value="%{#crumb.get('id')}"/></s:param></s:url>"><u><s:property value="%{#crumb.get('name')}" /></u>
	</a>
	/
</s:iterator>
