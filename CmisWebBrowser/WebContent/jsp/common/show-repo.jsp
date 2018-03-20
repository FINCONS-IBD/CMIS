<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<s:include value="/jsp/common/header.jsp" />
<div class="panel with-nav-tabs panel-success">
    <div class="panel-heading">
		<ul class="nav nav-tabs">
		  <li class="active"><a data-toggle="tab" href="#syntacticTab">Syntactic Processing</a></li>
		  <li><a data-toggle="tab" href="#semanticTab">Semantic Processing</a></li>
		</ul>
	 </div>
	  <div class="tab-content" style="overflow:inherit; padding-left:20px">
	
		<div id="syntacticTab" class="tab-pane fade in active">
			<s:include value="/jsp/common/search.jsp" />
			<s:include value="/jsp/common/root.jsp" />
			<s:include value="/jsp/common/navi.jsp" />
			
			<hr />
			
			<div class="row　offset1">
				<s:form method="POST">
					<s:hidden name="parent" value="%{folder}"></s:hidden>
					<s:hidden name="logic" value="add"></s:hidden>
					<s:submit value="%{getText('upload')}"
						action="ShowUpdateDocument"
						cssClass="btn btn-psy" />
					<s:submit value="%{getText('create_folder')}"
						action="ShowUpdateFolder" cssClass="btn btn-psy" />
				</s:form>
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
			
			<s:if test="showPrevious == true ">
				<a
					href="
					<s:url action="ShowFolder">
						<s:param name="folder"><s:property value="folder" /></s:param>
						<s:param name="skipCount"><s:property value="previousSkipCount" /></s:param>
					</s:url>
				">
					&lt; Previous </a>
			</s:if>
			   <s:text name="total" />:&nbsp;
			<s:property value="totalNumberOfNodes" />
			<s:text name="contents" />
			<s:if test="showNext == true ">
				<a
					href="
					<s:url action="ShowFolder">
						<s:param name="folder"><s:property value="folder" /></s:param>
						<s:param name="skipCount"><s:property value="nextSkipCount" /></s:param>
					</s:url>
				">
					Next &gt; </a>
			</s:if>
		</div>
		<div id="semanticTab" class="tab-pane fade">
			<s:include value="/jsp/common/search_semantic.jsp"/>
		</div>
	</div>
</div>

<!-- after the rendering of the page, include the javascript for the query builder -->
<script src="public/javascripts/querybuilder/demo.js"></script>

<s:include value="/jsp/common/footer.jsp" />