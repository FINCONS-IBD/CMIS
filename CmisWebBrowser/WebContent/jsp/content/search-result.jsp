<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<s:include value="/jsp/common/root.jsp" />
<hr />

<div class="container">
	<div class="row">
		<div class="span4">
			results:
			<s:property value="count"></s:property>
		</div>
		<div class="span8">
			filters:
			<s:property value="filterList" />
		</div>
	</div>
	
	<hr />
	
	<!-- Nodes -->
	<div class="row">
	<table class="nemaki span8">
	
	<s:iterator value="nodes" var="node">
	<tr>
		<div class="row">
			<s:if test="#node.folder == true">
				<s:push value="#{'node':node}">
					<s:include value="/jsp/folder/show-folder.jsp" />
				</s:push>
			</s:if>
			<s:else>
				<s:push value="#{'node':node}">
					<s:include value="/jsp/document/show-document.jsp" />
				</s:push>
			</s:else>
		</div>
	</tr>
	</s:iterator>
	</table>
	</div>
	
	<hr />
	<br clear="all" />
	<!-- Previous page / Next page -->
	<p>
		<!--<s:if test="showPrevious == true ">
			<a
				href="
				<s:url action="ShowFolder">
					<s:param name="folder"><s:property value="folder" /></s:param>
					<s:param name="skipCount"><s:property value="previousSkipCount" /></s:param>
				</s:url>
			">
				&lt; Previous </a>
		</s:if>-->
	    <s:text name="total" />:&nbsp;
	    <s:property value="count" />
		<!--<s:property value="totalNumberOfNodes" /> -->
		<s:text name="contents" />
		<!--<s:if test="showNext == true ">
			<a
				href="
				<s:url action="ShowFolder">
					<s:param name="folder"><s:property value="folder" /></s:param>
					<s:param name="skipCount"><s:property value="nextSkipCount" /></s:param>
				</s:url>
			">
				Next &gt; </a>
		</s:if>-->
	</p>
	
	<div class="row">
	<div>
		<button class="btn btn-psy" onclick="window.history.back();">Back</button>
	</div>
	</div>
	
	
</div>


