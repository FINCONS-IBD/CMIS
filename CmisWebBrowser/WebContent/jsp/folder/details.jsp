<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<s:include value="/jsp/common/root.jsp" />
<hr />

<div class="span6">
<div class="row">
	<div>
		<s:label cssClass="label" value="%{getText('id')}" />
		<p>${id}</p>
	</div>
</div>

<div class="row">
	<div>
		<s:label cssClass="label" value="%{getText('name')}" />
		<p>${name}</p>
	</div>
</div>

<div class="row">
	<div>
		<s:label cssClass="label" value="%{getText('path')}" />
		<p>${path}</p>
	</div>
</div>

<div class="row">
	<div>
		<s:label cssClass="label" value="%{getText('type')}" />
		<p>${type}</p>
	</div>
</div>

<div class="row">
	<div>
	<s:include value="/jsp/content/show-aspects.jsp" />
	</div>
</div>

<div class="row">
	<div>
		<s:label cssClass="label" value="%{getText('permissions')}" />
			<table class="nemaki span5" style="position: relative; clear: both;">
				<s:iterator value="acl" var="ace">
					<tr>
						<td class="span2"><s:property value="principalId" /></td>
						<td><s:property value="permissions" /></td>
					</tr>
				</s:iterator>
			</table>
			<div></div>
			<br />
	</div>
</div>

<div class="row">
	<div>
		<s:label cssClass="label" value="%{getText('created_by')}" />
		<p>${createdBy}</p>
	</div>
</div>

<div class="row">
	<div>
		<s:label cssClass="label" value="%{getText('creation')}" />
		<p>
			<s:date name="creationDate" format="yyyy/MM/dd HH:mm:ss" />
		</p>
	</div>
</div>

<div class="row">
	<div>
		<s:label cssClass="label" value="%{getText('last_modified_by')}" />
		<p>${lastModifiedBy}</p>
	</div>
</div>

<div class="row">
	<div>
		<s:label cssClass="label" value="%{getText('last_modification')}" />
		<p>
			<s:date name="lastModificationDate" format="yyyy/MM/dd HH:mm:ss" />
		</p>
	</div>
</div>

<div class="row">
	<div>
		<s:form method="POST">
			<s:submit value="%{getText('back')}" action="ShowFolder" cssClass="btn btn-psy" />
			<s:hidden name="folder" value="%{parent}"></s:hidden>
		</s:form>
	</div>
</div>


</div>

<s:include value="/jsp/common/footer.jsp" />
