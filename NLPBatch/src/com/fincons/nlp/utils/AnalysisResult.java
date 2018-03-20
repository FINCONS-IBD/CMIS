package com.fincons.nlp.utils;

import java.util.Set;

public class AnalysisResult {
	String cleanedText;
	Set<EntityCustom> entityList;
	
	public String getCleanedText() {
		return cleanedText;
	}
	
	public void setCleanedText(String cleanedText) {
		this.cleanedText = cleanedText;
	}
	
	public Set<EntityCustom> getEntityList() {
		return entityList;
	}

	public void setEntityList(Set<EntityCustom> entityList) {
		this.entityList = entityList;
	}
	
}
