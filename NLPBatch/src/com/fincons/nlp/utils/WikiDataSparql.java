package com.fincons.nlp.utils;

import org.apache.log4j.Logger;

import com.fincons.nlp.utils.PropertiesHelper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

//import jp.aegif.struts2cmisexplorer.util.PropertyManager;
//import jp.aegif.struts2cmisexplorer.util.impl.PropertyManagerImpl;

public class WikiDataSparql {

	final static Logger logger = Logger.getLogger(WikiDataSparql.class);
	
	public static Triple getFromWikiData(String wikiDataId) {
		
		String sparqlEndopoint = PropertiesHelper.getProps().getProperty("endpointWikipediaUrl");
		String wikiDataSublcassOf = PropertiesHelper.getProps().getProperty("wikiDataSublcassOf");
		String wikiDataInstanceOf = PropertiesHelper.getProps().getProperty("wikiDataInstanceOf");
		String definedByURI = PropertiesHelper.getProps().getProperty("isDefinedByURI");
		
		//if the wikiId is related to an entity, retrieve the instanceOf props
		String queryStringEntity =
				"PREFIX bd: <http://www.bigdata.com/rdf#>\n" +
		        "PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
		        "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
		        "PREFIX wd: <http://www.wikidata.org/entity/>\n" +
		        "SELECT ?typeOf ?typeOfLabel WHERE {"
		        + "wd:"+wikiDataId+" " + wikiDataInstanceOf +" ?typeOf ."
		        + "SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\" }"
		        + "}";
		
		//if the wikiId is related to a class, retrieve the subclassOf props
		String queryStringClass =
				"PREFIX bd: <http://www.bigdata.com/rdf#>\n" +
		        "PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
		        "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
		        "PREFIX wd: <http://www.wikidata.org/entity/>\n" +
		        "SELECT ?typeOf ?typeOfLabel WHERE {"
		        + "wd:"+wikiDataId+" " + wikiDataSublcassOf +" ?typeOf ."
		        + "SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\" }"
		        + "}";
        
        String typeOfString = "";
        String labelString = "";
        
        Query query;
        QueryExecution qexec = null;
        ResultSet results;
        QuerySolution res;
        Triple result = null;
        
        try {
        	
        	
            query = QueryFactory.create(queryStringEntity);
            qexec = QueryExecutionFactory.sparqlService(sparqlEndopoint, query);
            
            results = qexec.execSelect();
            
            //if the WikiId is related to an entity, retrieve the InstanceOf Prop
            if(results.hasNext()){
            	res = results.next();
            	typeOfString = res.get("typeOf").toString();
            	labelString = res.get("typeOfLabel").toString();
            	labelString = labelString.substring(0,labelString.lastIndexOf('@'));
            }else{
            	//if the WikiId is related to a class, retrieve the subclassOf Prop
                query = QueryFactory.create(queryStringClass);
                qexec = QueryExecutionFactory.sparqlService(sparqlEndopoint, query);
                
                results = qexec.execSelect();
                if(results.hasNext()){
                	res = results.next();
                	typeOfString = res.get("typeOf").toString();
                	labelString = res.get("typeOfLabel").toString();
                	labelString = labelString.substring(0,labelString.lastIndexOf('@'));
                }
            }
            
            if(typeOfString != labelString && labelString != null){
            	Node subjectDBPedia = NodeFactory.createURI(typeOfString);
				Node predicateDBPedia = NodeFactory.createURI(definedByURI);
				Node objectDBPedia = NodeFactory.createLiteral(labelString);

				result = new Triple(subjectDBPedia, predicateDBPedia, objectDBPedia);
            }else{
            	logger.info("Error retrieving the info about the wikidata entity: " + wikiDataId +". The entity will be skipepd!");
            }
            	
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            qexec.close();
        }
		
        return result;
	}
	
	public static void main (String args[]){
		System.out.println(getFromWikiData("Q2931729"));
	}

}
