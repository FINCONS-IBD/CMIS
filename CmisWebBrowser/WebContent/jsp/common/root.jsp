<%@taglib uri="/struts-tags" prefix="s"%>

<!-- Link to the root -->
<s:if test="%{#session['logged-in'] == 'true'}">
	<div class="row message" style="margin-top: 5px">
		<div class="span4">
			<!--a href="<s:url action="ShowFolder"><s:param name="folderPath">/</s:param></s:url>"-->
			<a href="<s:url action="ShowFolder"><s:param name="folderPath"><s:property value="%{rootFolder}"/></s:param></s:url>">
				<s:text name="go_root"/>
			</a>
		</div>
	</div>
</s:if>
