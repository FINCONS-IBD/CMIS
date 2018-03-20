package com.fincons.nlp.utils;

import java.util.*;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

//import jp.aegif.struts2cmisexplorer.util.PropertyManager;
//import jp.aegif.struts2cmisexplorer.util.impl.PropertyManagerImpl;
import virtuoso.jena.driver.*;

public class VirtuosoClientApi
{
	final static Logger logger = Logger.getLogger(VirtuosoClientApi.class);

	public static boolean populateVirtuosoGraph(String graphName, List<Triple> triples){

		logger.info("Ready to populate the graph " + graphName + " with "+triples.size()+" triples");
		
		boolean success = false;
		
		String dbUser = PropertiesHelper.getProps().getProperty("virtuosoDbUser");
		String dbPassword = PropertiesHelper.getProps().getProperty("virtuosoDbPsw");
		String virtuosoEndPoint = PropertiesHelper.getProps().getProperty("virtuosoEndpoint");
		
		VirtGraph graph = new VirtGraph (graphName, virtuosoEndPoint, dbUser, dbPassword);

		for(Triple triple: triples)
			graph.add(triple);
		
		logger.info("Number of triples added sucesfull: " + graph.getCount());

		success=true;
		
		return success;
	}
	
	public static ResultSet sparqlQuery(String query){
		
		String dbUser = PropertiesHelper.getProps().getProperty("virtuosoDbUser");
		String dbPassword = PropertiesHelper.getProps().getProperty("virtuosoDbPsw");
		String virtuosoEndPoint = PropertiesHelper.getProps().getProperty("virtuosoEndpoint");

        VirtGraph set = new VirtGraph (virtuosoEndPoint, dbUser, dbPassword);

        Query sparql = QueryFactory.create(query);

        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, set);

        ResultSet results = vqe.execSelect();
        
        return results;
	}

	public static ResultSet sparqlQueryGraph(String graphName, String query){
		
		String dbUser = PropertiesHelper.getProps().getProperty("virtuosoDbUser");
		String dbPassword = PropertiesHelper.getProps().getProperty("virtuosoDbPsw");
		String virtuosoEndPoint = PropertiesHelper.getProps().getProperty("virtuosoEndpoint");

        VirtGraph set = new VirtGraph (graphName, virtuosoEndPoint, dbUser, dbPassword);

        Query sparql = QueryFactory.create(query);

        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, set);

        ResultSet results = vqe.execSelect();
        
        return results;
	}
    

}