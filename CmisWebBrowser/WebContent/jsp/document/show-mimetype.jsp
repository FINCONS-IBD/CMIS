<%@taglib uri="/struts-tags" prefix="s"%>


<s:set value="%{'public/images'}" var="image_path" />


<s:if test="mimetype == 'application/x-javascript'">
	<img src="<s:property value="image_path" />/js.gif" />
</s:if>
<s:elseif test="#node.mimetype == 'text/plain'">
	<img src="<s:property value="image_path" />/text-file-32.png" />
</s:elseif>
<s:elseif test="#node.mimetype == 'application/msword'">
	<img src="<s:property value="image_path" />/doc-file-32.png" />
</s:elseif>
<s:elseif test="#node.mimetype == 'text/xml'">
	<img src="<s:property value="image_path" />/xml.gif" />
</s:elseif>
<s:elseif test="#node.mimetype == 'image/gif'">
	<img src="<s:property value="image_path" />/img-file-32.png" />
</s:elseif>
<s:elseif test="#node.mimetype == 'image/jpeg'">
	<img src="<s:property value="image_path" />/img-file-32.png" />
</s:elseif>
<s:elseif test="#node.mimetype == 'image/jpeg2000'">
	<img src="<s:property value="image_path" />/jpg.gif" />
</s:elseif>
<s:elseif test="#node.mimetype == 'video/mpeg'">
	<img src="<s:property value="image_path" />/mpeg.gif" />
</s:elseif>
<s:elseif test="#node.mimetype == 'audio/x-mpeg'">
	<img src="<s:property value="image_path" />/mpg.gif" />
</s:elseif>
<s:elseif test="#node.mimetype == 'video/mp4'">
	<img src="<s:property value="image_path" />/mp4.gif" />
</s:elseif>
<s:elseif test="#node.mimetype == 'video/mpeg2'">
	<img src="<s:property value="image_path" />/mp2.gif" />
</s:elseif>
<s:elseif test="#node.mimetype == 'application/pdf'">
	<img src="<s:property value="image_path" />/pdf-file-32.png" />
</s:elseif>
<s:elseif test="#node.mimetype == 'image/png'">
	<img src="<s:property value="image_path" />/img-file-32.png" />
</s:elseif>
<s:elseif test="#node.mimetype == 'application/vnd.powerpoint'">
	<img src="<s:property value="image_path" />/ppt-file-32.png" />
</s:elseif>
<s:elseif test="#node.mimetype == 'audio/x-wav'">
	<img src="<s:property value="image_path" />/wmv.gif" />
</s:elseif>
<s:elseif test="#node.mimetype == 'application/vnd.excel'">
	<img src="<s:property value="image_path" />/xls-file-32.png" />
</s:elseif>
<s:elseif test="#node.mimetype == 'application/zip'">
	<img src="<s:property value="image_path" />/zip.gif" />
</s:elseif>
<s:elseif
	test="#node.mimetype == 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'">
	<img src="<s:property value="image_path" />/doc-file-32.png" />
</s:elseif>
<s:elseif
	test="#node.mimetype == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'">
	<img src="<s:property value="image_path" />/xls-file-32.png" />
</s:elseif>
<s:elseif
	test="#node.mimetype == 'application/vnd.openxmlformats-officedocument.presentationml.presentation'">
	<img src="<s:property value="image_path" />/ppt-file-32.png" />
</s:elseif>
<s:else>
	<img src="<s:property value="image_path" />/generic-file-32.png" />
</s:else>
