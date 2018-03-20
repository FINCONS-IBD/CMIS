<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" src="public/javascripts/aspectstree.js"></script>

<s:label for="name" cssClass="label" value="%{getText('aspect')}"></s:label>

<s:iterator value="aspects" status="aspectIndex">
	<p class="treeName" id="aspect_${aspectIndex.index}">
		<u><s:property value="%{name}" /></u>
	</p>
	<s:hidden value="%{name}"
		name="modifiedAspects[%{#aspectIndex.count - 1}].name"></s:hidden>

	<div class="" id="aspect_${aspectIndex.index}">
		<table class="nemaki span5" style="position: relative; clear: both;">
			<s:iterator value="properties" status="propertyIndex">
				<tr>
					<td class="span2"><s:property value="%{key}" /></td>
					<td><s:property value="%{value}" /><br /></td>
				</tr>
			</s:iterator>
		</table>
		<div style="clear: both"></div>
		<br />
		<br />
	</div>

</s:iterator>