<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<s:include value="/jsp/common/root.jsp" />
<s:include value="/jsp/common/navi.jsp" />
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
			<s:label cssClass="label" value="%{getText('size')}" />
			<p>
				<s:property value="%{humanReadableByteCount(size, true)}" />
			</p>
		</div>
	</div>

	<div class="row">
		<div>
			<s:include value="/jsp/content/show-aspects.jsp" />
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
		<s:include value="/jsp/content/show-versions.jsp" />
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
			<s:label cssClass="label" value="Tika Metadata" />
			<table class="nemaki span5" style="position: relative; clear: both;">
				<s:iterator value="tikaMetadata" var="tika">
					<tr>
						<td class="span2"><s:property value="key" /></td>
						<td><s:property value="value" /></td>
					</tr>
				</s:iterator>
			</table>
			<div></div>
			<br />
		</div>
	</div>
<!--
	<div class="row">
		<div>
			<s:label cssClass="label" value="Entity Recognition" />
			<table class="nemaki span5" style="position: relative; clear: both;">
				<tr>
			    	<th>MatchedText</th>
			    	<th>DBPediaType</th> 
			    	<th>WikiDataId</th>
			    	<th>RelevanceScore</th>
			    	<th>ConfidenceScore</th>
			    	<th>Occourences</th>
			    	<th>Sentence</th>
			  	</tr>
				<s:iterator value="textRazorAnalysis" var="razor">
					<tr>
						<td class="span2"><s:property value="matchedText" /></td>
						<td><s:property value="dbPediaTypes" /></td>
						<td><s:property value="wikiDataId" /></td>
						<td><s:property value="relevanceScore" /></td>
						<td><s:property value="confidenceScore" /></td>
						<td><s:property value="matchingTokens" /></td>
						<td><s:property value="sentence" /></td>
					</tr>
				</s:iterator>
			</table>
			<div></div>
			<br />
		</div>
	</div>
-->	
	<br />
	<div class="row">
		<div>
			<s:form method="POST">
				<s:submit value="%{getText('back')}" action="ShowFolder" cssClass="btn btn-psy" />
			<!-- 
				<script type="text/javascript">
				function printJSON(){
					alert('Look here: '+data);
					var data = '${jsonAnalysis}';
					var json = JSON.parse(data);
					var json_string = JSON.stringify(json);
					console.log(json_string);
					alert('Check the console log to read the json result string');
				}
				</script>
				<input id="ShowDocumentDetails_ShowFolder" class="btn btn-psy" onclick="javascript:printJSON();" value="Save in Virtuoso" type="button"> 
			-->
				<s:hidden name="folder" value="%{parent}"></s:hidden>				
			</s:form>
			
		</div>
		
	</div>

</div>

<s:include value="/jsp/common/footer.jsp" />
