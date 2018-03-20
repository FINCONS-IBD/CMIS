<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div>
	<s:label cssClass="label" value="%{getText('versions')}" />

	<s:if test="%{versionInfo.size gte 0}">
		<p>
			<s:set name="latestNumber" value="5" scope="session" />
		<table class="nemaki span5">
			<s:iterator value="versionInfo" var="val" status="st">
				<s:if test="%{#st.count lte #session['latestNumber']}">
					<tr>
						<td class="span1"><a
							href="<s:url action="SendFile"><s:param name="node">${id}</s:param></s:url>">Versions<s:property
									value="%{versions.length - #st.count}" /></a></td>
						<td class="span1">${createdBy}</td>
						<td class="span1"><s:property
								value="%{convertToDate(creationDate)}" /></td>
						<td class="span1"><s:property
								value="%{humanReadableByteCount(size, true)}" /></td>
					</tr>
				</s:if>
			</s:iterator>
		</table>

		<div class="clear"></div>
		<br />

		<s:if test="%{versionInfo.size gt 5}">
			<p class="treeName" id="versionHistory">
				<u>>><s:property value="%{getText('oldversions')}" /></u>
			</p>
			<!-- save aspects' number for a further iteration -->
			<s:set name="latestNumber" value="5" scope="session" />

			<div id="versionHistory" style="display: none">


				<!-- TODO 設定値を外に切り出す -->

				<table class="nemaki span5">
					<s:iterator value="versionInfo" var="version" status="st">
						<s:if test="%{#st.count gt 5}">
							<tr>
								<td class="span1"><a
									href="<s:url action="SendFile"><s:param name="node">${id}</s:param></s:url>">Versions<s:property
											value="%{versions.length - #st.count}" /></a></td>
								<td class="span1">${createdBy}</td>
								<td class="span1"><s:property
										value="%{convertToDate(creationDate)}" /></td>
								<td class="span1"><s:property
										value="%{humanReadableByteCount(size, true)}" /></td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
			</div>
		</s:if>
	</s:if>

</div>