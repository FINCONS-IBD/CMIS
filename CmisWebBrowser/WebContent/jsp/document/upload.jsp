<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<s:include value="/jsp/common/root.jsp" />

<!-- Upload Form -->
<div class="row">
	<div class="span4 offset2" style="margin-top: 3%">
		<s:form method="POST" enctype="multipart/form-data">
			<s:label for="name" cssClass="label" value="%{getText('name')}"></s:label>
			<s:textfield name="name" style=" height: auto; margin-left: 4px"></s:textfield>
			<br /><br />
			<s:label for="upload" cssClass="label" value="%{getText('file')}"></s:label>
			<s:file name="upload" label="File"></s:file>
			<br /><br />
			<s:submit value="%{getText('execute')}" action="UpdateDocument" cssClass="btn btn-psy" />
            &nbsp;&nbsp;
			<s:submit value="%{getText('cancel')}" action="ShowFolder" cssClass="btn  btn-psy" />
			<s:hidden name="type" value="%{type}"></s:hidden>
			<s:hidden name="parent" value="%{parent}"></s:hidden>
			<s:hidden name="logic" value="%{logic}"></s:hidden>
			<s:hidden name="folder" value="%{parent}"></s:hidden>
		</s:form>
	</div>
</div>

<s:include value="/jsp/common/footer.jsp" />
