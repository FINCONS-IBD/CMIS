package com.fincons.nlp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;

public class TextRazorVirtuosoHelper {
	
	final static Logger logger = Logger.getLogger(TextRazorVirtuosoHelper.class);

	public static boolean convertAnalysisInTriples(String graphName, Set<EntityCustom> list_entities_mapped){

		logger.info("Starting the graph population with "+list_entities_mapped.size()+" textrazor entities...");
		
		String definedByURI = PropertiesHelper.getProps().getProperty("isDefinedByURI");
		String hasLabelURI = PropertiesHelper.getProps().getProperty("hasLabelURI");
		String conceptGraphURI = PropertiesHelper.getProps().getProperty("conceptGraphURI");

		List <Triple> triples = new ArrayList <Triple> ();
		
		List <Triple> concept_triples = new ArrayList <Triple> ();

//		Node subject = NodeFactory.createURI(conceptGraphURI);
		
		String subjectURI;
		String rdfUriType;
		Triple wikiDataTriple;
		for(EntityCustom entity : list_entities_mapped){
			
			subjectURI = createSubjectURI(graphName, entity);
			
			//add the human readable label to the subject
			Node subjectDBPediaForTheLabel = NodeFactory.createURI(subjectURI);
			Node predicateDBPediaForTheLabel = NodeFactory.createURI(hasLabelURI);
			Node objectDBPediaForTheLabel = NodeFactory.createLiteral(entity.getMatchedText());
			
			triples.add(new Triple(subjectDBPediaForTheLabel, predicateDBPediaForTheLabel, objectDBPediaForTheLabel));
			
			Node subjectDBPedia = NodeFactory.createURI(subjectURI);
			if(entity.getDbPediaTypes()!= null){
				//dbpedia
				for(String type : entity.getDbPediaTypes()){
					rdfUriType = createDBPediaURI(type);

					Node predicateDBPedia = NodeFactory.createURI(definedByURI);
					Node objectDBPedia = NodeFactory.createURI(rdfUriType);

					triples.add(new Triple(subjectDBPedia, predicateDBPedia, objectDBPedia));
					
					Node subject = NodeFactory.createLiteral(type);
					concept_triples.add(new Triple(objectDBPedia, predicateDBPedia, subject));
					
				}
			}else{
				//wikidata
				String wikiDataId = entity.getWikiDataId();
				wikiDataTriple = WikiDataSparql.getFromWikiData(wikiDataId);
				
				if (wikiDataTriple != null){
					concept_triples.add(wikiDataTriple);
					triples.add(new Triple(subjectDBPedia, wikiDataTriple.getPredicate(), wikiDataTriple.getSubject()));
				}
			}
			
		}

		boolean result = VirtuosoClientApi.populateVirtuosoGraph(graphName, triples);
		
		boolean result_concepts = VirtuosoClientApi.populateVirtuosoGraph(conceptGraphURI, concept_triples);
		
		return result && result_concepts;

	}

	private static String createDBPediaURI(String type) {
		String dbPediaRootURI = PropertiesHelper.getProps().getProperty("dbPediaRootURL");
		return dbPediaRootURI+type;
	}

	private static String createSubjectURI(String graphURI, EntityCustom entity) {
		String entityURI = graphURI;

		String matchedText = entity.getMatchedText().replaceAll(" ", "_");
		
		String entity_tag = "#"+matchedText+"_"+entity.getStartPos()+"_"+entity.getEndPos();

		entityURI+=entity_tag;

		return entityURI;

	}	


}
