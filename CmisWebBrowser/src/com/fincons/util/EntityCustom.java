package com.fincons.util;

import java.util.ArrayList;
import java.util.List;

public class EntityCustom {
	
	private int idEntityCustom;
	private String matchedText;
	private List<String> dbPediaTypes;
	private String wikiDataId;
	private double relevanceScore;
	private double confidenceScore;
	private List<Integer> matchingTokens;
	private List<String> sentence;
	private int startPos;
	private int endPos;
	
	public EntityCustom(int idEntityCustom, String matchedText, List<String> dbPediaTypes, String wikiDataId,
			double relevanceScore, double confidenceScore, List<Integer> matchingTokens,
			int startPos, int endPos) {
		super();
		this.idEntityCustom = idEntityCustom;
		this.matchedText = matchedText;
		this.dbPediaTypes = dbPediaTypes;
		this.wikiDataId = wikiDataId;
		this.relevanceScore = relevanceScore;
		this.confidenceScore = confidenceScore;
		this.matchingTokens = matchingTokens;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public int getIdEntityCustom() {
		return idEntityCustom;
	}

	public void setIdEntityCustom(int idEntityCustom) {
		this.idEntityCustom = idEntityCustom;
	}

	public String getMatchedText() {
		return matchedText;
	}

	public void setMatchedText(String matchedText) {
		this.matchedText = matchedText;
	}

	public List<String> getDbPediaTypes() {
		return dbPediaTypes;
	}

	public void setDbPediaTypes(List<String> dbPediaTypes) {
		this.dbPediaTypes = dbPediaTypes;
	}

	public double getRelevanceScore() {
		return relevanceScore;
	}

	public void setRelevanceScore(double relevanceScore) {
		this.relevanceScore = relevanceScore;
	}

	public double getConfidenceScore() {
		return confidenceScore;
	}

	public void setConfidenceScore(double confidenceScore) {
		this.confidenceScore = confidenceScore;
	}

	public List<Integer> getMatchingTokens() {
		return matchingTokens;
	}

	public void setMatchingTokens(List<Integer> matchingTokens) {
		this.matchingTokens = matchingTokens;
	}

	public List<String> getSentence() {
		return sentence;
	}

	public void setSentence(List<String> sentence) {
		this.sentence = sentence;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public String getWikiDataId() {
		return wikiDataId;
	}

	public void setWikiDataId(String wikiDataId) {
		this.wikiDataId = wikiDataId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idEntityCustom;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityCustom other = (EntityCustom) obj;
		if (idEntityCustom != other.idEntityCustom)
			return false;
		return true;
	}
	
	
	
}
