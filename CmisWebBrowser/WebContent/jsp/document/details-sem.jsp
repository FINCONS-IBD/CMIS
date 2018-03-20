<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
 
<s:include value="/jsp/common/header.jsp" />
<s:include value="/jsp/common/root.jsp" />
<s:include value="/jsp/common/navi.jsp" />

<div class="row" style="margin-bottom:15px">
	<hr />
	<button class="btn btn-psy" onclick="window.history.back();">Back</button>
</div>


<div class="container">

	<div class="row">
		<div class ="col-md-7">
			<div class="row"> 
				<s:label cssClass="label" value="Semantic Triples Details" />
				<table class="nemaki span6" style="position: relative; clear: both;">
					<tr>
				    	<th>
				    		<div class="span2">Subject</div>
				    	</th>
				    	<th>
				    		<div class="span2">Predicate</div>
				    	</th> 
				    	<th>
				    		<div class="span2">Object URI</div>
				    	</th>
				    	<th>
				    		<div class="span2">Object Name</div>
				    	</th>
				  	</tr>
					<s:iterator value="semanticTriples" var="razor">
						<tr>
							<td>
								<div class="span2" style="word-break: break-all;">
									<a href='<s:property value="subject" />'><s:property value="subject" /></a>
								</div>
							</td>
							<td>
								<div class="span2" style="word-break: break-all;">
									<a href='<s:property value="predicate" />'><s:property value="predicate" /></a>
								</div>
							</td>
							<td>
								<div class="span2" style="word-break: break-all;">
									<!-- <a href='<s:property value="object" />'><s:property value="object" /></a></td> -->																	
								 	<c:if test="${fn:contains(object, 'http:')}">
										<a href='<s:property value="object" />'><s:property value="object" /></a>
									</c:if>
								 	<c:if test="${!fn:contains(object, 'http:')}">
										<s:property value="object" />
									</c:if>	
								</div>						
							</td>
							<td>
								<div class="span2" style="word-break: break-all;">
									<s:property value="labelReadable" />
								</div>
							</td>							
						</tr>
					</s:iterator>
				</table>
				<div></div>
				<br />
			</div>
		
		</div>
	
		<s:include value="/jsp/common/pieChartDoc.jsp" />
	</div>
	
	<br />
	<div class="row">
		<div>
		<input id="GoBackButton" class="btn btn-psy" onclick="window.history.back();" value="Back" type="button">
		<!--
			<s:form method="POST">
				<s:submit value="%{getText('back')}" action="ShowFolder" cssClass="btn btn-psy" />
				
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
		
				<s:hidden name="folder" value="%{parent}"></s:hidden>			
			</s:form>
		-->
		</div>
		
	</div>
</div>

<s:include value="/jsp/common/footer.jsp" />
