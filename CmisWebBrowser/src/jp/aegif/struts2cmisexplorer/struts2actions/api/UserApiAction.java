package jp.aegif.struts2cmisexplorer.struts2actions.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jp.aegif.struts2cmisexplorer.domain.Credentials;
import jp.aegif.struts2cmisexplorer.domain.exceptions.NotLoggedInException;
import jp.aegif.struts2cmisexplorer.struts2actions.AuthenticatedAction;
import jp.aegif.struts2cmisexplorer.struts2actions.util.ApiUtil;
import jp.aegif.struts2cmisexplorer.util.PropertyManager;
import jp.aegif.struts2cmisexplorer.util.impl.PropertyManagerImpl;

import org.apache.struts2.interceptor.ParameterAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.tachibanakikaku.serverconnection.ServerConnection;
import com.tachibanakikaku.serverconnection.ServerConnectionException;

public class UserApiAction extends AuthenticatedAction implements ParameterAware {

	private static final long serialVersionUID = 2467957386324162529L;

	private ServerConnection conn;

	private static final String BASEPATH = "/Nemaki/rest/user/";
	
	private static final String NEMAKI_PROTOCOL = "nemaki.protocol";
	private static final String NEMAKI_HOST = "nemaki.host";
	private static final String NEMAKI_PORT = "nemaki.port";

	private Map<String, String[]> parameters;
	
	private JSONObject result;
	
	/**
	 * 
	 */
	public String execute() throws Exception {
		String logic = new String();
		try{
			logic = (String)(parameters.get("logic"))[0];
		}catch(Exception e){
			e.printStackTrace();
			return Action.ERROR;
		}
		
		try {
			PropertyManager propertyManager = new PropertyManagerImpl();
			String protocol = propertyManager.readValue(NEMAKI_PROTOCOL);
			String host = propertyManager.readValue(NEMAKI_HOST);
			Integer port = Integer.parseInt(propertyManager.readValue(NEMAKI_PORT));
			
			conn = ServerConnection.createServerConnection(protocol,host, port);
		} catch (Exception e) {
			e.printStackTrace();
			return Action.ERROR;
		}
		
		if(logic.equals("list") || logic.equals("")){
			return executeList();
		}else if(logic.equals("create")){
			return executeCreate(parameters);
		}else if(logic.equals("update")){
			return executeUpdate(parameters);
		}else if(logic.equals("delete")){
			return executeDelete(parameters);
		}else{
			return Action.ERROR;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private String executeList(){
		//Get JSON response via REST API
		JSONArray users = getUserList();
		
		//Output for jTable
		JSONObject result = new JSONObject();
		if(users != null){
			result.put("Result", "OK");
			
			if (parameters.get("jtSorting") != null){
				users = ApiUtil.sortList(users, parameters.get("jtSorting")[0]);
			}
			
			result.put("Records", users);
		}else{
			result.put("Result", "ERROR");
		}

		setResult(result);
		 
		return Action.SUCCESS;
	}
	
	/**
	 * 
	 * @return
	 */
	private String executeCreate(Map<String, String[]> parameters){
		//Get the target user's id
		String id = extractId(parameters);
		if(id == null) return Action.ERROR;
		
		//Convert input parameters
		HashMap<String, Object> restParams = buildParameters(parameters);

		//Set creator
		String currentUser;
		try {
			currentUser = getCredentials().getUser();
			restParams.put("creator", currentUser);
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}
		
		//Call REST API
		JSONObject obj = new JSONObject();
		try {
			String urlString = BASEPATH + "create/" + id;
			obj = conn.postJSON(urlString, restParams);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
		}
		
		//Output for jTable
		JSONObject result = new JSONObject();
		if(obj.get("status").equals("success")){
			result.put("Result", "OK");
			//Set the created user info for jTable
			result.put("Record",getUser(id));
		}else{
			result.put("Result", "ERROR");
			result.put("Message", getErrMsg(obj));
		}
		
		setResult(result);

		return Action.SUCCESS;
	}
	
	/**
	 * 
	 * @return
	 */
	private String executeUpdate(Map<String, String[]> parameters) {
		//Get the target user's id
		String id = extractId(parameters);
		if(id == null) return Action.ERROR;
		
		//Convert input parameters
		HashMap<String, Object> restParams = buildParameters(parameters);

		//Set modifier
		String currentUser;
		try {
			currentUser = getCredentials().getUser();
			restParams.put("modifier", currentUser);
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}

		//Add admin permission parameters if the user is Admin
		restParams = addAdminParams(restParams);

		//Call REST API
		JSONObject obj = new JSONObject();
		String urlString = BASEPATH + "update/" + id;
		try {
			obj = conn.putJSON(urlString, restParams);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
		}

		//Output for jTable
		JSONObject result = new JSONObject();
		if (obj.get("status").equals("success")) {
			result.put("Result", "OK");
			// Set the updated user info for jTable
			result.put("Record", getUser(id));
		} else {
			result.put("Result", "ERROR");
			result.put("Message", getErrMsg(obj));
		}
		
		setResult(result);
		
		return Action.SUCCESS;
	}

	/**
	 * 
	 * @return
	 */
	private String executeDelete(Map<String, String[]> parameters) {
		//Get the target user's id
		String id = extractId(parameters);
		if(id == null) return Action.ERROR;
		
		//Convert input parameters
		HashMap<String, Object> restParams = buildParameters(parameters);

		//Add admin permission parameters if the user is Admin
		restParams = addAdminParams(restParams);

		//Call REST API
		JSONObject obj = new JSONObject();
		String urlString = BASEPATH + "delete/" + id;
		try {
			obj = conn.deleteJSON(urlString, restParams);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
		}

		//Output for jTable
		JSONObject result = new JSONObject();
		if (obj.get("status").equals("success")) {
			result.put("Result", "OK");
		} else {
			result.put("Result", "ERROR");
			result.put("Message", getErrMsg(obj));
		}
		setResult(result);
		return Action.SUCCESS;
	}
	

	/**
	 * Get users list JSON via REST API from Nemaki Server
	 * @return
	 */
	public JSONArray getUserList(){
		try {
			JSONObject obj = new JSONObject();
			String urlString = BASEPATH + "list";
			obj = conn.getJSON(urlString, new HashMap<String, Object>());
			if(obj.get("status").equals("success")){
				JSONArray users = (JSONArray) obj.get("users");
				return users;
			}else{
				return null;
			}
		} catch (ServerConnectionException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get a user JSON via REST API from Nemaki Server
	 * @param id
	 * @return
	 */
	public JSONObject getUser(String id){
		JSONArray users = getUserList();
		if(users == null) return null;
			
		Iterator<?> iterator = users.iterator();
		while(iterator.hasNext()){
			JSONObject user = (JSONObject) iterator.next();
			if(user.get("userId").equals(id)){
				return user;
			}
		}
		return null;
	}
	
	/**
	 * Utility
	 */
	private HashMap<String,Object> addAdminParams(HashMap<String,Object>params){
		try {
			Credentials credentials = getCredentials();
			params.put("admin", credentials.getUser());
			params.put("adminpass", credentials.getPassword());
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}
		return params;
	}
	
	private String extractId(Map<String, String[]> parameters){
		String id = parameters.get("userId")[0];
		if (id != null && !id.equals("")) {
			return id;
		} else {
			return null;
		}
	}

	/**
	 * Convert a map of parameter for HttpRequest's Body, which requires Map<String,Object>.
	 * And at the same time, adjust some parameters.
	 * @param parameters
	 * @return
	 */
	private HashMap<String,Object> buildParameters(Map<String,String[]>parameters){
		//remove unnecessary parameters
		if(parameters.get("logic") != null) parameters.remove("logic");
		if(parameters.get("userId") != null) parameters.remove("userId");
		
		//build
		HashMap<String,Object> builtParams = new HashMap<String, Object>();
		for(String _key : parameters.keySet()){
			String _val = parameters.get(_key)[0];
			//mapping between input parameter and REST parameter 
			if(_key.equals("userName")) _key = "name";

			builtParams.put(_key, _val);
		}
		return builtParams;
	}
	
	
	private String getErrMsg(JSONObject obj){
		try {
			JSONArray err = (JSONArray) obj.get("error");
			return err.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}
	
	
	/**
	 * Getter & Setter
	 */
	
	@Override
	public void setParameters(Map<String, String[]> arg0) {
		this.parameters = arg0;
	}
	
	public JSONObject getResult() {
		return result;
	}

	public void setResult(JSONObject result) {
		this.result = result;
	}
	
	public void setConn(ServerConnection conn) {
		this.conn = conn;
	}
}
