package com.fincons.nlp.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textrazor.annotations.Entity;
import com.textrazor.AnalysisException;
import com.textrazor.NetworkException;
import com.textrazor.TextRazor;
import com.textrazor.annotations.AnalyzedText;
import com.textrazor.annotations.Sentence;
import com.textrazor.annotations.Word;

public class TextRazorApi {

	private static final Log logger = LogFactory
			.getLog(TextRazorApi.class);

	public static AnalysisResult testAnalysis(String apiKey, String idFile, String documentContent) throws NetworkException, AnalysisException {

		List<String> dbPediaTypes;
		String wikiDataId;

		//Get the DBPedia Properties to insert anycase
		String propsToForce = PropertiesHelper.getProps().getProperty("propsToForce");
		
		Double relevanceScore = Double.parseDouble(PropertiesHelper.getProps().getProperty("relevanceScore"));
		
		String[] values = propsToForce.split(",");
		List<String> dbPediaMainProps = new ArrayList<String>(Arrays.asList(values));

		TextRazor client = new TextRazor(apiKey);

		client.addExtractor("words");
		client.addExtractor("entities");

		client.setEnrichmentQueries(Arrays.asList("fbase:/location/location/geolocation>/location/geocode/latitude", "fbase:/location/location/geolocation>/location/geocode/longitude"));
		
//		client.setCleanupMode("cleanHTML");
		client.setCleanupMode("stripTags");
		client.setCleanupReturnCleaned(true);
		client.setCleanupReturnRaw(true);
		
		AnalyzedText response = client.analyze(documentContent);
		
		if(response.isOk()){

			Set<EntityCustom> list_entities_mapped = new LinkedHashSet<EntityCustom>();

			for (Sentence sentence : response.getResponse().getSentences()) {

				List<String> word_sentence = new ArrayList<String>();
				
				for (Word word : sentence.getWords()) {

					word_sentence.add(word.getToken());

					for (Entity entity : word.getEntities()) {

						dbPediaTypes = entity.getDBPediaTypes();
						wikiDataId = entity.getWikidataId();
						
						//add the entity with a dbPedia property considered important
						if(dbPediaTypes != null){
							dbPediaTypes.retainAll(dbPediaMainProps);
							if(!dbPediaTypes.isEmpty()){

								EntityCustom entityCustom = new EntityCustom(
										entity.getId(),
										entity.getMatchedText(),
										entity.getDBPediaTypes(), 
										entity.getWikidataId(),
										entity.getRelevanceScore(),
										entity.getConfidenceScore(),
										entity.getMatchingTokens(),
										entity.getStartingPos(),
										entity.getEndingPos());

								entityCustom.setSentence(word_sentence);

								list_entities_mapped.add(entityCustom); 			
								
								break;
							}
						}
						
						//add only the entity with relevanceScore > 0.4
						if(entity.getRelevanceScore() >= relevanceScore){
							EntityCustom entityCustom = new EntityCustom(
									entity.getId(),
									entity.getMatchedText(),
									entity.getDBPediaTypes(), 
									entity.getWikidataId(),
									entity.getRelevanceScore(),
									entity.getConfidenceScore(),
									entity.getMatchingTokens(),
									entity.getStartingPos(),
									entity.getEndingPos());

							entityCustom.setSentence(word_sentence);

							list_entities_mapped.add(entityCustom); 	
						}
							

//						if(dbPediaTypes != null || wikiDataId != null){
//
//							//add the entity with a dbPedia property considered important
//							if(entity.getRelevanceScore() == 0 && entity.getConfidenceScore() == 0.5 ){
//								if(dbPediaTypes == null)
//									break;
//								else{
//									dbPediaTypes.retainAll(dbPediaMainProps);
//									if(dbPediaTypes.isEmpty())
//										break;
//								}
//							}		
//				
//						}
					}

				}
			}

			logger.debug("****** Found " +list_entities_mapped.size()+" entities! ***********");
				
			AnalysisResult result = new AnalysisResult();
			result.setCleanedText(response.getResponse().getCleanedText());
			result.setEntityList(list_entities_mapped);
			
			return result;
		}
		else 
			return null;

	}

	


	/**
	 * @param args
	 * @throws NetworkException 
	 */
	public static void main(String[] args) throws NetworkException, AnalysisException {
	}

}
