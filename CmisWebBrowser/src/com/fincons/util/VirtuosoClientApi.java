package com.fincons.util;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

import virtuoso.jena.driver.*;

public class VirtuosoClientApi
{

	public static boolean populateVirtuosoGraph(String graphName, List<Triple> triples){

		boolean success = false;

		String dbUser = PropertiesHelper.getProps().getProperty("virtuosoDbUser");
		String dbPassword = PropertiesHelper.getProps().getProperty("virtuosoDbPsw");
		String virtuosoEndPoint = PropertiesHelper.getProps().getProperty("virtuosoEndpoint");

		VirtGraph graph = new VirtGraph (graphName, virtuosoEndPoint, dbUser, dbPassword);

		for(Triple triple: triples)
			graph.add(triple);

		System.out.println("Number of triples added sucesfull: " + graph.getCount());

		success=true;

		return success;
	}

	public static ResultSet sparqlQuery(String query){

		String dbUser = PropertiesHelper.getProps().getProperty("virtuosoDbUser");
		String dbPassword = PropertiesHelper.getProps().getProperty("virtuosoDbPsw");
		String virtuosoEndPoint = PropertiesHelper.getProps().getProperty("virtuosoEndpoint");

		VirtGraph set = new VirtGraph (virtuosoEndPoint, dbUser, dbPassword);
		ResultSet results = null;
		try{

			Query sparql = QueryFactory.create(query);
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, set);
			results = vqe.execSelect();

		}catch(Exception e){
			e.printStackTrace();
		}


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

	public static String getDocJSONFromVirtuoso(String graphUrl) {
		
		String dbUser = PropertiesHelper.getProps().getProperty("virtuosoDbUser");
		String dbPassword = PropertiesHelper.getProps().getProperty("virtuosoDbPsw");
		String virtuosoEndPoint = PropertiesHelper.getProps().getProperty("virtuosoEndpoint");

		String conceptGraph = PropertiesHelper.getProps().getProperty("conceptGraphURI");
		String isDefinedBy = PropertiesHelper.getProps().getProperty("isDefinedByURI");

		VirtGraph set = new VirtGraph (virtuosoEndPoint, dbUser, dbPassword);

		String count_concepts =  "SELECT (COUNT(?x) AS ?counter) \n"
				+ "FROM NAMED<"+graphUrl+"> \n"
				+ "FROM NAMED<"+conceptGraph+"> \n"
				+ "WHERE {\n"
				+ "GRAPH <"+graphUrl+"> \n"
				+ "{?subject <"+isDefinedBy+"> ?object .} \n"
				+ "GRAPH <"+conceptGraph+"> \n"
				+ "{?object ?predicate ?x .}\n"
				+ "}";
				
//SELECT (COUNT(?x) AS ?counter)
//FROM NAMED<http://89.207.106.74:8092/CmisWebBrowser/processed/873de794-4f1d-459e-97c4-1fd56a107324.html> 
//FROM NAMED<http://89.207.106.75:8890/conceptsGraph> 
//WHERE {
//GRAPH <http://89.207.106.74:8092/CmisWebBrowser/processed/873de794-4f1d-459e-97c4-1fd56a107324.html> 
//{?subject <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> ?object .} 
//GRAPH <http://89.207.106.75:8890/conceptsGraph> 
//{?object ?predicate ?x .} 
//}
//		
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (count_concepts, set);
		
		ResultSet results = vqe.execSelect();
		
		Literal C_12_literal;
		int total_concept = 0;
				
		if(results.hasNext()) {
			QuerySolution result = results.nextSolution();
			C_12_literal = ((Literal) result.get("counter"));
			total_concept = C_12_literal.getInt();
		}
		
		
		String query_counter = "SELECT ?x (COUNT(?x) AS ?counter) \n"
				+ "FROM NAMED<"+graphUrl+"> \n"
				+ "FROM NAMED<"+conceptGraph+"> \n"
				+ "WHERE {\n"
				+ "GRAPH <"+graphUrl+"> \n"
				+ "{?subject <"+isDefinedBy+"> ?object .} \n"
				+ "GRAPH <"+conceptGraph+"> \n"
				+ "{?object ?predicate ?x .} \n"
				+ "} GROUP BY ?x HAVING(((xsd:float(COUNT(?x))/"+total_concept+")*100) > 1)";				

/*
 * SELECT ?x (COUNT(?x) AS ?counter) 
FROM NAMED<http://89.207.106.74:8092/CmisWebBrowser/processed/31606183-11b6-47f0-ae77-1f4d20b9282a.html> 
FROM NAMED<http://89.207.106.75:8890/conceptsGraph> 
WHERE {
GRAPH <http://89.207.106.74:8092/CmisWebBrowser/processed/31606183-11b6-47f0-ae77-1f4d20b9282a.html> 
{?subject <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> ?object .} 
GRAPH <http://89.207.106.75:8890/conceptsGraph> 
{?object ?predicate ?x .} 
} GROUP BY ?x HAVING(((xsd:float(COUNT(?x))/TOT)*100) > 3)		
 */

		
		String query_others = "SELECT (SUM(?counter) AS ?counter) WHERE"
				+ "{SELECT ?x (COUNT(?x) AS ?counter) \n"
				+ "FROM NAMED<"+graphUrl+"> \n"
				+ "FROM NAMED<"+conceptGraph+"> \n"
				+ "WHERE {\n"
				+ "GRAPH <"+graphUrl+"> \n"
				+ "{?subject <"+isDefinedBy+"> ?object .} \n"
				+ "GRAPH <"+conceptGraph+"> \n"
				+ "{?object ?predicate ?x .} \n"
				+ "} GROUP BY ?x HAVING(((xsd:float(COUNT(?x))/"+total_concept+")*100) <= 1)}\n";		

/*
 * SELECT (SUM(?counter) AS ?counter) WHERE
{SELECT ?x (COUNT(?x) AS ?counter) FROM NAMED<http://89.207.106.74:8092/CmisWebBrowser/processed/31606183-11b6-47f0-ae77-1f4d20b9282a.html> 
FROM NAMED<http://89.207.106.75:8890/conceptsGraph> 
WHERE {
GRAPH <http://89.207.106.74:8092/CmisWebBrowser/processed/31606183-11b6-47f0-ae77-1f4d20b9282a.html> 
{?subject <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> ?object .} 
GRAPH <http://89.207.106.75:8890/conceptsGraph> 
{?object ?predicate ?x .} 
} GROUP BY ?x HAVING (COUNT(?x) <= 3)}		
 */
		
/*
		String query = "SELECT ?x ?counter \n"
				+ "WHERE\n"
				+ "{\n"
				+ "{SELECT ?x (COUNT(?x) AS ?counter) \n"
				+ "FROM NAMED<"+graphUrl+"> \n"
				+ "FROM NAMED<"+conceptGraph+"> \n"
				+ "WHERE {\n"
				+ "GRAPH <"+graphUrl+"> \n"
				+ "{?subject <"+isDefinedBy+"> ?object .} \n"
				+ "GRAPH <"+conceptGraph+"> \n"
				+ "{?object ?predicate ?x .} \n"
				+ "} GROUP BY ?x HAVING (COUNT(?x) > 3)}\n"
				+ "UNION\n"
				+ "{SELECT 'Others' AS ?x (SUM(?counter) AS ?counter) WHERE\n"
				+ "{SELECT ?x (COUNT(?x) AS ?counter) "
				+ "FROM NAMED<"+graphUrl+"> \n"
				+ "FROM NAMED<"+conceptGraph+"> \n"
				+ "WHERE {\n"
				+ "GRAPH <"+graphUrl+"> \n"
				+ "{?subject <"+isDefinedBy+"> ?object .} \n"
				+ "GRAPH <"+conceptGraph+"> \n"	
				+ "{?object ?predicate ?x .} \n"
				+ "} GROUP BY ?x HAVING (COUNT(?x) <= 3)}}\n"
				+ "}\n"
				+ "ORDER BY DESC(?counter)";				
		

 * SELECT ?x ?counter
WHERE
{
{SELECT ?x (COUNT(?x) AS ?counter) 
FROM NAMED<http://89.207.106.74:8092/CmisWebBrowser/processed/8395d4cf-a228-4938-b46d-ea22ba9fb7dc.html> 
FROM NAMED<http://89.207.106.75:8890/conceptsGraph> 
WHERE {
GRAPH <http://89.207.106.74:8092/CmisWebBrowser/processed/8395d4cf-a228-4938-b46d-ea22ba9fb7dc.html> 
{?subject <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> ?object .} 
GRAPH <http://89.207.106.75:8890/conceptsGraph> 
{?object ?predicate ?x .} 
} GROUP BY ?x HAVING (COUNT(?x) > 3) ORDER BY DESC(?counter)}
UNION
{SELECT 'Others' AS ?x (SUM(?counter) AS ?counter) WHERE
{SELECT ?x (COUNT(?x) AS ?counter) 
FROM NAMED<http://89.207.106.74:8092/CmisWebBrowser/processed/8395d4cf-a228-4938-b46d-ea22ba9fb7dc.html> 
FROM NAMED<http://89.207.106.75:8890/conceptsGraph> 
WHERE {
GRAPH <http://89.207.106.74:8092/CmisWebBrowser/processed/8395d4cf-a228-4938-b46d-ea22ba9fb7dc.html> 
{?subject <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> ?object .} 
GRAPH <http://89.207.106.75:8890/conceptsGraph> 
{?object ?predicate ?x .} 

} GROUP BY ?x HAVING (COUNT(?x) <= 3)}}
}
ORDER BY DESC(?counter)
 */
		
	
		vqe = VirtuosoQueryExecutionFactory.create (query_counter, set);
		
		results = vqe.execSelect();
		
		
		RDFNode node;
		int count = 0;
		String objectUrl = null;
		JSONArray ja = new JSONArray();
		
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			
			node =  result.get("x");
			objectUrl = node.toString();
			
			C_12_literal = ((Literal) result.get("counter"));
			count = C_12_literal.getInt();
			
			JSONObject jo = new JSONObject();
			jo.put("label", objectUrl);
			jo.put("value", count);

			ja.put(jo);	
		}
		
		
		vqe = VirtuosoQueryExecutionFactory.create (query_others, set);	
		results = vqe.execSelect();
		
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			
			C_12_literal = ((Literal) result.get("counter"));
			count = C_12_literal.getInt();
			
			JSONObject jo = new JSONObject();
			jo.put("label", "Others");
			jo.put("value", count);

			ja.put(jo);	

		}
		
//		String jsonString = "[{\"label\": \"Positive docs\",\"value\": 22},	{\"label\": \"Negative docs\",\"value\": 33}];";
		
		String jsonString = ja.toString();
		
		return jsonString;
	}

	public static int getTotalDocsCount() {

		String prefix = PropertiesHelper.getProps().getProperty("processedFileLocation");
		
		String statement = "SELECT (COUNT(DISTINCT(?graph)) AS ?resCount) WHERE {GRAPH ?graph {?subject ?predicate ?object} "
				+ "FILTER(contains(str(?graph), \""+prefix+"\"))}"; 

		ResultSet results = VirtuosoClientApi.sparqlQuery(statement);
		
		QuerySolution rs = results.nextSolution();
		
		Literal C_12_literal = ((Literal) rs.get("resCount"));
		
		return C_12_literal.getInt();
		
	}

	public static ResultSet retrieveDocDetails(String graphUrl) {
		
		String dbUser = PropertiesHelper.getProps().getProperty("virtuosoDbUser");
		String dbPassword = PropertiesHelper.getProps().getProperty("virtuosoDbPsw");
		String virtuosoEndPoint = PropertiesHelper.getProps().getProperty("virtuosoEndpoint");

		String conceptGraph = PropertiesHelper.getProps().getProperty("conceptGraphURI");
		
		VirtGraph set = new VirtGraph (virtuosoEndPoint, dbUser, dbPassword);

//		SELECT ?subject ?predicate ?object ?label
//		FROM NAMED<http://89.207.106.74:8092/CmisWebBrowser/processed/873de794-4f1d-459e-97c4-1fd56a107324.html>
//		FROM NAMED<http://89.207.106.75:8890/conceptsGraph>
//		WHERE {
//		GRAPH <http://89.207.106.74:8092/CmisWebBrowser/processed/873de794-4f1d-459e-97c4-1fd56a107324.html>
//		{?subject ?predicate ?object}
//		GRAPH <http://89.207.106.75:8890/conceptsGraph>
//		{?object ?predicate ?label}
//		} 
		
		String query = "SELECT ?subject ?predicate ?object ?label \n"
				+ "FROM NAMED<"+graphUrl+"> \n"
				+ "FROM NAMED<"+conceptGraph+"> \n"
				+ "WHERE {\n"
				+ "GRAPH <"+graphUrl+"> \n"
				+ "{?subject ?predicate ?object} \n"
				+ "optional {GRAPH <"+conceptGraph+"> \n"
				+ "{?object ?predicate ?label}} \n"
				+ "}";		

		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, set);
		
		ResultSet results = vqe.execSelect();
		
		return results;

	}

}