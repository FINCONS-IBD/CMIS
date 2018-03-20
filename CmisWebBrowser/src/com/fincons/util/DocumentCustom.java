package com.fincons.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentCustom {

	Map<String, String> metadataMap;
	private Set<EntityCustom> contentAnalysis;
	private String cmisDocumentId;
	private String cmisDownloadUrl;
	
	public Map<String, String> getMetadataMap() {
		return metadataMap;
	}
	public void setMetadataMap(Map<String, String> metadataMap) {
		this.metadataMap = metadataMap;
	}
	public Set<EntityCustom> getContentAnalysis() {
		return contentAnalysis;
	}
	public void setContentAnalysis(Set<EntityCustom> contentAnalysis) {
		this.contentAnalysis = contentAnalysis;
	}
	public String getCmisDocumentId() {
		return cmisDocumentId;
	}
	public void setCmisDocumentId(String cmisDocumentId) {
		this.cmisDocumentId = cmisDocumentId;
	}
	public String getCmisDownloadUrl() {
		return cmisDownloadUrl;
	}
	public void setCmisDownloadUrl(String cmisDownloadUrl) {
		this.cmisDownloadUrl = cmisDownloadUrl;
	}
		
}
