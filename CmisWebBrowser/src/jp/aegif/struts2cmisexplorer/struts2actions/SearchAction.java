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
import org.apache.chemistry.opencmis.server.support.query.CalendarHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Search content
 * 
 * @author mryoshio
 * 
 */
public class SearchAction extends SearchActionBase {

	private static final long serialVersionUID = 6621205354654725317L;
	private static final Log log = LogFactory.getLog(SearchAction.class);

	private static final String CREATED_BY = "createdBy";
	private static final String LAST_MODIFIED_BY ="lastModifiedBy";
	private static final String CREATION_DATE ="creationDate";
	private static final String LAST_MODIFICATION_DATE ="lastModificationDate";
	private static final String CONTAINS ="contains";

	private static final String  EQUAL = "equal";
	private static final String  NOT_EQUAL = "not_equal";
	private static final String  LESS = "less";
	private static final String  LESS_OR_EQUAL = "less_or_equal"; 
	private static final String  GREATER = "greater";
	private static final String  GREATER_OR_EQUAL= "greater_or_equal";
	private static final String  BETWEEN = "between";

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
		String convertedMin ="";
		String convertedMax ="";
		Calendar cal;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		
		for(int i = 0; i<rules.length(); i++){
			query_filters += " AND ";
			
			rule = rules.getJSONObject(i);

			fieldId = rule.get("field").toString();
			fieldOperator =  rule.get("operator").toString();
			
			switch(fieldId) {

			case CREATED_BY:
			case LAST_MODIFIED_BY:
				fieldValue = rule.get("value").toString();
				if(fieldOperator.equals(EQUAL))
					query_filters += "cmis:"+ fieldId+ "='"+fieldValue+"'";
				else
					query_filters += "cmis:"+ fieldId+ "!='"+fieldValue+"'";
				break;

			case CREATION_DATE:
			case LAST_MODIFICATION_DATE:
				
				switch(fieldOperator){
				case EQUAL:
					fieldValue = rule.get("value").toString();
					date = df.parse(fieldValue);
					cal = Calendar.getInstance();
					cal.setTime(date);
					convertedMin = CalendarHelper.toString(cal);
					
					cal.set(Calendar.HOUR_OF_DAY, 23); 
			        cal.set(Calendar.MINUTE, 59); 
			        cal.set(Calendar.SECOND, 59); 
			        cal.set(Calendar.MILLISECOND, 999); 
					
					convertedMax = CalendarHelper.toString(cal);
					
					query_filters += " cmis:"+fieldId +">= TIMESTAMP '" + convertedMin
							+ "' AND cmis:"+fieldId+"<= TIMESTAMP '"+convertedMax+"'";
					break;
				case NOT_EQUAL:
					fieldValue = rule.get("value").toString();
					date = df.parse(fieldValue);
					cal = Calendar.getInstance();
					cal.setTime(date);
					convertedMin = CalendarHelper.toString(cal);
					
					cal.set(Calendar.HOUR_OF_DAY, 23); 
			        cal.set(Calendar.MINUTE, 59); 
			        cal.set(Calendar.SECOND, 59); 
			        cal.set(Calendar.MILLISECOND, 999); 
					
					convertedMax = CalendarHelper.toString(cal);
					
					query_filters += " (cmis:"+fieldId +"> TIMESTAMP '" + convertedMax  
							+ "' OR cmis:"+fieldId+"< TIMESTAMP '"+convertedMin+"')";
					break;
				case LESS:
					fieldValue = rule.get("value").toString();
					date = df.parse(fieldValue);
					cal = Calendar.getInstance();
					cal.setTime(date);
					convertedMin = CalendarHelper.toString(cal);
					query_filters += " cmis:"+fieldId +"< TIMESTAMP '" + convertedMin+"'";
					break;
				case LESS_OR_EQUAL:
					fieldValue = rule.get("value").toString();
					date = df.parse(fieldValue);
					cal = Calendar.getInstance();
					cal.setTime(date);
					
					cal.set(Calendar.HOUR_OF_DAY, 23); 
			        cal.set(Calendar.MINUTE, 59); 
			        cal.set(Calendar.SECOND, 59); 
			        cal.set(Calendar.MILLISECOND, 999); 
					
					convertedMax = CalendarHelper.toString(cal);
					
					query_filters += " cmis:"+fieldId+"<= TIMESTAMP '"+convertedMax+"'";

					break;
				case GREATER:
					fieldValue = rule.get("value").toString();
					date = df.parse(fieldValue);
					cal = Calendar.getInstance();
					cal.setTime(date);
					
					cal.set(Calendar.HOUR_OF_DAY, 23); 
			        cal.set(Calendar.MINUTE, 59); 
			        cal.set(Calendar.SECOND, 59); 
			        cal.set(Calendar.MILLISECOND, 999); 
					
					convertedMax = CalendarHelper.toString(cal);
					
					query_filters += " cmis:"+fieldId+"> TIMESTAMP '"+convertedMax+"'";
					
					break;
				case GREATER_OR_EQUAL:
					fieldValue = rule.get("value").toString();
					date = df.parse(fieldValue);
					cal = Calendar.getInstance();
					cal.setTime(date);
					convertedMin = CalendarHelper.toString(cal);
					query_filters += " cmis:"+fieldId +"> TIMESTAMP '" + convertedMin+"'";

					break;
				case BETWEEN:
					//contains two values, min and max date. for example ["2016-09-14","2016-09-22"]
					fieldValue = rule.get("value").toString();
					
					JSONArray jr = new JSONArray(fieldValue);
					
					date = df.parse(jr.getString(0));
					cal = Calendar.getInstance();
					cal.setTime(date);
					convertedMin = CalendarHelper.toString(cal);
					
					date = df.parse(jr.getString(1));
					cal = Calendar.getInstance();
					cal.setTime(date);
					convertedMax = CalendarHelper.toString(cal);

					query_filters += " cmis:"+fieldId +"<= TIMESTAMP '" + convertedMax
							+ "' AND cmis:"+fieldId+">= TIMESTAMP '"+convertedMin+"'";
					
					break;
				}
				
				break;

			case CONTAINS:
				fieldValue = rule.get("value").toString();
				if(fieldOperator.equals(EQUAL))
					query_filters += " CONTAINS ('" + fieldValue+"') ";
				else
					query_filters += " NOT CONTAINS ('" + fieldValue+"') ";
					
				break;
						     
			}


			/*
			 *    {
      "id": "createdBy",
      "field": "createdBy",
      "type": "string",
      "input": "text",
      "operator": "equal",
      "value": "LeonardoS"
    },
			 */
		}

/*
		String[] filters = filterListItems.split(",");
		String query_filters = "";
		String fieldName = "";
		String contentField = "";
		String onlyDate = "";
		String offsetTimeZone="";
		String dateNoOffset = "";
		for(int i = 0;i<filters.length;i++){
			query_filters += " AND ";
			if(filters[i].toUpperCase().contains("CONTAINS"))
				query_filters += "CONTAINS('"+filters[i].split("=")[1]+"')";
			else if(filters[i].toUpperCase().contains("DATE")){
				fieldName = "cmis:"+filters[i].split("=")[0];
				contentField = filters[i].split("=")[1];
				onlyDate = contentField.split("T")[0];
				offsetTimeZone = contentField.split("\\+")[1];
				dateNoOffset = contentField.split("\\+")[0];
				query_filters += fieldName +">= TIMESTAMP '"+dateNoOffset+".000+"+offsetTimeZone
						+ "' AND "+fieldName+"<= TIMESTAMP '"+onlyDate+"T23:59:59.999+"+offsetTimeZone+"'";
			}
			else 
				query_filters += "cmis:"+filters[i].split("=")[0]+"='"+filters[i].split("=")[1]+"'";
		}

*/
		String statement = "SELECT * FROM cmis:document where cmis:objectTypeId='cmis:document'" + query_filters;	

		//by leo: set the maxItemPerPage to a big number to remove the paginator on the search results
		facade.getSession().getDefaultContext().setMaxItemsPerPage(999999999);
		ItemIterable<QueryResult> results = facade.getSession().query(statement,false);
		for (Iterator<QueryResult> it = results.iterator(); it.hasNext();) {
			QueryResult r = it.next();
			Node n = new Node();

			n.setId(String.valueOf(r.getPropertyById(PropertyIds.OBJECT_ID)
					.getFirstValue()));
			n.setType(String.valueOf(r.getPropertyById(PropertyIds.OBJECT_TYPE_ID)
					.getFirstValue()));
			n.setName(String.valueOf(r.getPropertyById(PropertyIds.NAME)
					.getFirstValue()));

			//TODO: can't set correct permissions without Nemaki PermissionService getFiltered() method.
			//      getDocuments() method should return all properties?(at present: id, type, name) 
			//Set result's nodes
			//Set filer null, because an inadequate filter hinders getNodeListPage method   
			OperationContext context = getFacade().getSession().getDefaultContext();
			context.setFilter(null);
			Node node = facade.getNode(n.getId());
			getNodes().add(node);
		}
		//		setCount(results.getPageNumItems());
		setCount(results.getTotalNumItems());
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

	//	public long getTotalNumberOfNodes() {
	//		return page.getTotalNumberOfNodes();
	//	}

	//	public void setSkipCount(int skipCount) {
	//		this.skipCount = skipCount;
	//	}
}
