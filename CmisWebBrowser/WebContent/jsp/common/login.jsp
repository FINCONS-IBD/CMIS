<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<!--s:include value="/jsp/common/root.jsp" /-->

<!-- Login form -->
<s:if test="loggedIn != true">
	<div class="row" align="center" style="margin-top: 3%">
		<div class="span4 offset4">
			<s:form action="Login" method="POST">
				
				<table>
					<tr>
						<td><s:label for="name" cssClass="label" value="%{getText('user')}"></s:label></td>
						<td><s:textfield style=" height: auto; margin-left: 4px" name="user" maxlength="40" /></td>
					</tr>
					<tr>
						<td>
							<s:label for="password" cssClass="label" value="%{getText('passwd')}"></s:label>
						</td>
						<td>
							<s:password style=" height: auto; margin-left: 4px" name="password" maxlength="40" />
							<s:hidden name="folderPath" value="/" />
						</td>
						
					</tr>
					<tr>
						<td>
							<s:submit value="%{getText('login')}" cssClass="btn btn-psy" />
						</td>
					</tr>
				</table>
			</s:form>
		</div>
	</div>
</s:if>

<s:if test="hasActionErrors()">
   <div class="errors" align="center">
      <s:actionerror/>
   </div>
</s:if>

<s:include value="/jsp/common/footer.jsp" />
