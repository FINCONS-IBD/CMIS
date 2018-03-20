package jp.aegif.struts2cmisexplorer.struts2actions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

import jp.aegif.struts2cmisexplorer.domain.Node;
import jp.aegif.struts2cmisexplorer.domain.NodesListPage;
import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.domain.exceptions.NotLoggedInException;
import jp.aegif.struts2cmisexplorer.opencmisbinding.OpenCMISRepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.struts2actions.base.SearchActionBase;
import sun.util.resources.cldr.he.CalendarData_he_IL;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.server.support.query.CalendarHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fincons.util.PropertiesHelper;
import com.fincons.util.VirtuosoClientApi;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Search content
 * 
 * @author mryoshio
 * 
 */
public class SearchActionSem extends SearchActionBase {

	private static final long serialVersionUID = 6631205354654725316L;
	private static final Log log = LogFactory.getLog(SearchActionSem.class);

	private static final String CONTAINS_TEXT = "containsText";
	private static final String CONTAINS_CONCEPT ="containsConcept";
	private static final String TITLE ="title";
	private static final String CREATOR ="creator";
	private static final String TOPIC ="topic";

	private static final String  EQUAL = "equal";
	private static final String  NOT_EQUAL = "not_equal";
	private static final String  LESS = "less";
	private static final String  LESS_OR_EQUAL = "less_or_equal"; 
	private static final String  GREATER = "greater";
	private static final String  GREATER_OR_EQUAL= "greater_or_equal";
	private static final String  BETWEEN = "between";
	private static final String  TALK_ABOUT = "talk_about";
	private static final String  NOT_TALK_ABOUT = "not_talk_about";

	/**
	 * Number of results that are skipped when displaying. It is used for paging
	 * results.
	 */
	private int skipCount = 0;

	/**
	 * Information about nodes that are to be displayed on the current page.
	 */
	private NodesListPage page;

	private String filterList;
	
	private int totalDocsCount = 0;

	@Override
	public String execute() throws Exception {
		log.debug("#execute(" + getTerm() + ")");
		try{
			query();
		}catch(Exception e){
			log.error("Exception occurred:", e);
			addActionError(getText("query_error"));
			return INPUT;
		}

		return SUCCESS;
	}

	private void query() throws Exception {
		OpenCMISRepositoryClientFacade facade = getFacade();
		log.debug("facade.getSession: " + facade.getSession());


		String filterListItems = getFilterList();

		JSONObject jsonObj = new JSONObject(filterListItems);

		JSONArray rules = jsonObj.getJSONArray("rules");
		JSONObject rule = null;
		String fieldId = ""; 
		String fieldOperator = "";
		String query_filters = "";
		String fieldValue;
		String predicate ="";

		for(int i = 0; i<rules.length(); i++){

			query_filters += "\n";

			rule = rules.getJSONObject(i);

			fieldId = rule.get("field").toString();
			fieldOperator =  rule.get("operator").toString();

			switch(fieldId){
			case CONTAINS_TEXT:
				predicate = "http://www.w3.org/2000/01/rdf-schema#label";
				break;
			case CONTAINS_CONCEPT:
				predicate = "http://www.w3.org/2000/01/rdf-schema#isDefinedBy";
				break;
			case TITLE:
				predicate = "http://purl.org/dc/elements/1.1/title";
				break;
			case CREATOR:
				predicate = "http://purl.org/dc/elements/1.1/creator";
				break;
			case TOPIC:
				predicate = "http://purl.org/dc/terms/subject";
				break;
			}

			switch(fieldId) {

			case CONTAINS_TEXT:
				fieldValue = rule.get("value").toString();
				if(fieldOperator.equals(EQUAL))
					query_filters += "?s_"+i+" <"+ predicate + "> \""+fieldValue+"\" .";
				else
					query_filters += "FILTER NOT EXISTS {?s_"+i+" <"+ predicate + "> \""+fieldValue+"\"} .";
				break;					
			case TITLE:
			case CREATOR:
				fieldValue = rule.get("value").toString();
				if(fieldOperator.equals(EQUAL))
					//{?s_0 <http://purl.org/dc/terms/subject> ?obj .FILTER (regex(str(?obj), "CMIS")) }
					query_filters += "{?s_"+i+" <"+ predicate + "> ?obj .FILTER (regex(str(?obj), \""+fieldValue+"\"))} .";
				else
					query_filters += "{?s_"+i+" <"+ predicate + "> ?obj .\n"
							+ "FILTER (!regex(str(?obj), \""+fieldValue+"\"))} .";
				break;		     
			case TOPIC:
				fieldValue = rule.get("value").toString();
				if(fieldOperator.equals(TALK_ABOUT))
					//{?s_0 <http://purl.org/dc/terms/subject> ?obj .FILTER (regex(str(?obj), "CMIS")) }
					query_filters += "{?s_"+i+" <"+ predicate + "> ?obj .FILTER (regex(str(?obj), \""+fieldValue+"\"))} .";
				else
					query_filters += "{?s_"+i+" <"+ predicate + "> ?obj .\n"
							+ "FILTER (!regex(str(?obj), \""+fieldValue+"\"))} .";
				break;	
			case CONTAINS_CONCEPT:
				fieldValue = rule.get("value").toString();
				if(fieldOperator.equals(EQUAL))
					query_filters += "?s_"+i+" <"+ predicate + "> <"+fieldValue+"> .";
				else
					query_filters += "FILTER NOT EXISTS {?s_"+i+" <"+ predicate + "> <"+fieldValue+">} .";
				break;				
			}
		}

		/*
SELECT  DISTINCT ?g WHERE  
{
GRAPH ?g 
{?s <http://www.w3.org/2000/01/rdf-schema#label> "Jeff" .
?p <http://www.w3.org/2000/01/rdf-schema#label> "CMIS" .
FILTER NOT EXISTS {?z <http://purl.org/dc/elements/1.1/creator> "Jeff Potts"} .
{?s_0 <http://purl.org/dc/terms/subject> ?obj .FILTER (regex(str(?obj), "CMIS")) }
} 
} ORDER BY  ?g
		 */

		String statement = "SELECT  DISTINCT ?g WHERE {GRAPH ?g {" + query_filters + "\n"
				+ "} } ORDER BY ?g";	

		//by leo: set the maxItemPerPage to a big number to remove the paginator on the search results
		facade.getSession().getDefaultContext().setMaxItemsPerPage(999999999);
		//		ItemIterable<QueryResult> results = facade.getSession().query(statement,false);

		String prefix_to_delete = PropertiesHelper.getProps().getProperty("processedFileLocation");
		String clear_id_from_url = "";

		ResultSet results = VirtuosoClientApi.sparqlQuery(statement);

		int count = 0;

		while (results.hasNext()) {
			
			QuerySolution rs = results.nextSolution();
			RDFNode g = rs.get("g");

			clear_id_from_url = g.toString().replace(prefix_to_delete,"");		    
			clear_id_from_url = clear_id_from_url.substring(0,clear_id_from_url.indexOf('.'));

			Node n = new Node();
			n.setId(clear_id_from_url);

			OperationContext context = getFacade().getSession().getDefaultContext();
			context.setFilter(null);
			try{
				Node node = facade.getNode(n.getId());
				getNodes().add(node);
				count++;
			}catch(CmisObjectNotFoundException e){
				log.info("Cmis document not found in the Content Management System:", e);
			}

		}
		//		setCount(results.getPageNumItems());
		setCount(count);


	}


	/**
	 * Whether the given object is a folder or not.
	 */
	//	private static boolean isFolder(CmisObject object) {
	//		return object.getBaseTypeId().value()
	//				.equals(ObjectType.FOLDER_BASETYPE_ID);
	//	}


	/**
	 *Paging utility 
	 */

	/**
	 * The "skip count" to be used if the user clicks on "Previous".
	 */
	//	public int getPreviousSkipCount() throws NotLoggedInException {
	//		int previousSkipCount = skipCount
	//				- getFacade().getMaxItemsPerPage();
	//		if (previousSkipCount < 0) {
	//			previousSkipCount = 0;
	//		}
	//		return previousSkipCount;
	//	}

	/**
	 * The "skip count" to be used if the user clicks on "Next".
	 */
	//	public int getNextSkipCount() throws NotLoggedInException {
	//		return skipCount + getFacade().getMaxItemsPerPage();
	//	}

	/**
	 * Whether the "Previous" button is needed.
	 */
	//	public boolean getShowPrevious() throws NotLoggedInException {
	//		return skipCount > 0;
	//	}

	/**
	 * Whether the "Next" button is needed.
	 */
	//	public boolean getShowNext() throws NotLoggedInException {
	//		return skipCount + getFacade().getMaxItemsPerPage() < page
	//				.getTotalNumberOfNodes();
	//	}


	/**
	 * Getters / Setters
	 */
	/*
	public List<Node> getNodes() {
		return page.getNodes();
	}
	 */


	public String getFilterList() {
		return filterList;
	}

	public void setFilterList(String filterList) {
		this.filterList = filterList;
	}


	public int getTotalDocsCount() {
				
		this.totalDocsCount = VirtuosoClientApi.getTotalDocsCount();
		
		return totalDocsCount;
	}

	
	//	public long getTotalNumberOfNodes() {
	//		return page.getTotalNumberOfNodes();
	//	}

	//	public void setSkipCount(int skipCount) {
	//		this.skipCount = skipCount;
	//	}
}
