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
		<p>
			削除
			<s:checkbox name="aspectDelChks[%{#aspectIndex.count - 1}]"
				value="false" />
			更新
			<s:checkbox name="aspectModChks[%{#aspectIndex.count - 1}]"
				value="true" />
		</p>

		<table class="nemaki span5" style="position: relative; clear: both;">
			<s:iterator value="properties" status="propertyIndex">
				<tr>
					<td class="span2"><s:property value="%{key}" /></td>
					<s:hidden value="%{key}"
						name="modifiedAspects[%{#aspectIndex.count - 1}].properties[%{#propertyIndex.count - 1}].key"></s:hidden>
					<td><s:textfield style=" height: auto; margin-left: 4px" value="%{value}" size="50"
							name="modifiedAspects[%{#aspectIndex.count - 1}].properties[%{#propertyIndex.count - 1}].value" /><br /></td>
				</tr>
			</s:iterator>
		</table>
		<div style="clear: both"></div>
		<br />
	</div>

</s:iterator>
<br />

<!-- save aspects' number for a further iteration -->
<s:set name="docAspectsSize" value="aspects.size()" scope="session" />

<p class="treeName" id="aspect_other">
	<i class="icon-plus"></i><s:property value="%{getText('available_aspects')}"/>
</p>
<div class="hide" id="aspect_other">
	<s:iterator value="otherAspects" status="otherAspectIndex">
		<p class="treeName"
			id="aspect_<s:property value="%{#session['docAspectsSize'] + #otherAspectIndex.count - 1 }" />">
			<u><s:property value="%{name}" /></u>
		</p>
		<s:hidden value="%{name}"
			name="modifiedAspects[%{#session['docAspectsSize'] + #otherAspectIndex.count - 1}].name"></s:hidden>

		<div class="hide"
			id="aspect_<s:property value="%{#session['docAspectsSize'] + #otherAspectIndex.count - 1 }" />">
			<p>
				削除
				<s:checkbox
					name="aspectDelChks[%{#session['docAspectsSize'] + #otherAspectIndex.count - 1}]"
					value="false" />
				<!-- デフォルトでは、新規カスタム属性の更新ボタンはOFF -->
				更新
				<s:checkbox
					name="aspectModChks[%{#session['docAspectsSize'] + #otherAspectIndex.count - 1}]"
					value="false" />
			</p>

			<table class="nemaki span5" style="position: relative; clear: both;">
				<s:iterator value="properties" status="propertyIndex">
					<tr>
						<td class="span2"><s:property value="%{key}" /></td>
						<s:hidden value="%{key}"
							name="modifiedAspects[%{#session['docAspectsSize'] + #otherAspectIndex.count - 1}].properties[%{#propertyIndex.count - 1}].key"></s:hidden>
						<td><s:textfield style=" height: auto; margin-left: 4px" value="%{value}" size="50"
								name="modifiedAspects[%{#session['docAspectsSize'] + #otherAspectIndex.count - 1}].properties[%{#propertyIndex.count - 1}].value" /><br /></td>
					</tr>
				</s:iterator>
			</table>
			<div style="clear: both"></div>
			<br />
		</div>

	</s:iterator>
</div>