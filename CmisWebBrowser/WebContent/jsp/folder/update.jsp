<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<s:include value="/jsp/common/root.jsp" />
<hr />

<!-- Update Form -->
<div class="row">
	<div class="span6">
		<s:form method="POST" enctype="multipart/form-data">
			<s:label for="name" cssClass="label" value="%{getText('name')}"></s:label>
			<s:textfield name="name" label="Name" style=" height: auto; margin-left: 4px" value="%{name}"></s:textfield>
			<br /><br />
			
			<s:include value="/jsp/content/update-aspects.jsp"></s:include>
			
			
			
			<s:submit value="%{getText('execute')}" action="UpdateFolder" cssClass="btn btn-psy" />
			&nbsp;&nbsp;
			<s:submit value="%{getText('cancel')}" action="ShowFolder" cssClass="btn btn-psy" />
			<s:hidden name="id" value="%{id}"></s:hidden>
            <s:hidden name="logic" value="%{logic}"></s:hidden>
            <s:hidden name="folder" value="%{parent}"></s:hidden>
            <s:hidden name="parent" value="%{parent}"></s:hidden>
		</s:form>
	</div>
</div>

<s:include value="/jsp/common/footer.jsp" />
