package jp.aegif.struts2cmisexplorer.struts2actions.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.aegif.struts2cmisexplorer.domain.Credentials;
import jp.aegif.struts2cmisexplorer.domain.Group;
import jp.aegif.struts2cmisexplorer.domain.exceptions.NotLoggedInException;
import jp.aegif.struts2cmisexplorer.struts2actions.AuthenticatedAction;
import jp.aegif.struts2cmisexplorer.struts2actions.util.ApiUtil;
import jp.aegif.struts2cmisexplorer.util.PropertyManager;
import jp.aegif.struts2cmisexplorer.util.impl.PropertyManagerImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ParameterAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.tachibanakikaku.serverconnection.ServerConnection;
import com.tachibanakikaku.serverconnection.ServerConnectionException;

public class GroupApiAction extends AuthenticatedAction implements
ParameterAware {

	private static final long serialVersionUID = 2467957386324162529L;

	private ServerConnection conn;

	private static final String BASEPATH = "/Nemaki/rest/group/";

	private static final String NEMAKI_PROTOCOL = "nemaki.protocol";
	private static final String NEMAKI_HOST = "nemaki.host";
	private static final String NEMAKI_PORT = "nemaki.port";

	private Map<String, String[]> parameters;

	private JSONObject result;

	public String execute() throws Exception {
		String logic = "";
		try {
			logic = (String) (parameters.get("logic"))[0];
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PropertyManager propertyManager = new PropertyManagerImpl();
			String protocol = propertyManager.readValue(NEMAKI_PROTOCOL);
			String host = propertyManager.readValue(NEMAKI_HOST);
			Integer port = Integer.parseInt(propertyManager
					.readValue(NEMAKI_PORT));

			conn = ServerConnection
					.createServerConnection(protocol, host, port);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (logic.equals("list") || logic.equals("")) {
			return executeList();
		} else if (logic.equals("create")) {
			return executeCreate(parameters);
		} else if (logic.equals("update")) {
			return executeUpdate(parameters);
		} else if (logic.equals("delete")) {
			return executeDelete(parameters);
		} else if (logic.equals("memberUserlist")) {
			return executeMemberList(parameters);
		} else if (logic.equals("addMember")) {
			return executeAddMember(parameters);
		} else if (logic.equals("removeMember")) {
			return executeRemoveMember(parameters);
		} else {
			return Action.ERROR;
		}
	}

	/**
	 * 
	 * @return
	 */
	private String executeList() {
		// Get JSON response via REST API
		JSONArray groups = getGroupList();


		// Output for jTable
		JSONObject result = new JSONObject();
		if (groups != null) {
			result.put("Result", "OK");

			if (parameters.get("jtSorting") != null){
				groups = ApiUtil.sortList(groups, parameters.get("jtSorting")[0]);
			}
			result.put("Records", groups);
		} else {
			result.put("Result", "ERROR");
		}

		setResult(result);

		return Action.SUCCESS;
	}

	/**
	 * 
	 * @return
	 */
	private String executeCreate(Map<String, String[]> parameters) {
		// Get the target group's id
		String id = extractId(parameters);
		if (id == null)
			return Action.ERROR;

		// Convert input parameters
		HashMap<String, Object> restParams = buildParameters(parameters);

		// Set creator
		String currentUser;
		try {
			currentUser = getCredentials().getUser();
			restParams.put("creator", currentUser);
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}

		// Call REST API
		JSONObject obj = new JSONObject();
		String urlString = BASEPATH + "create/" + id;
		try {
			obj = conn.postJSON(urlString, restParams);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
		}

		// Output for jTable
		JSONObject result = new JSONObject();
		if (obj.get("status").equals("success")) {
			result.put("Result", "OK");
			// Set the created user info for jTable
			result.put("Record", getGroup(id));
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
	private String executeUpdate(Map<String, String[]> parameters) {
		// Get the target group's id
		String id = extractId(parameters);
		if (id == null)
			return Action.ERROR;

		// Convert input parameters
		HashMap<String, Object> restParams = buildParameters(parameters);

		// Set modifier
		String currentUser;
		try {
			currentUser = getCredentials().getUser();
			restParams.put("modifier", currentUser);
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}

		// Call REST API
		JSONObject obj = new JSONObject();
		String urlString = BASEPATH + "update/" + id;
		try {
			obj = conn.putJSON(urlString, restParams);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
		}

		// Output for jTable
		JSONObject result = new JSONObject();
		if (obj.get("status").equals("success")) {
			result.put("Result", "OK");
			// Set the updated user info for jTable
			result.put("Record", getGroup(id));

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
		// Get the target group's id
		String id = extractId(parameters);
		if (id == null)
			return Action.ERROR;

		// Convert input parameters
		HashMap<String, Object> restParams = buildParameters(parameters);

		// Call REST API
		JSONObject obj = new JSONObject();
		String urlString = BASEPATH + "delete/" + id;
		try {
			obj = conn.deleteJSON(urlString, restParams);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
		}

		// Output for jTable
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

	private String executeMemberList(Map<String, String[]> parameters) {
		// Get the target group's id
		String groupId = extractId(parameters);
		if (groupId == null)
			return Action.ERROR;
		JSONObject group = getGroup(groupId);

		try {
			// Retrieve the user members info from JSON
			List<String> memberUsers = (List<String>) group.get("users");
			// Retrieve the group members info from JSON
			List<String> memberGroups = (List<String>) group.get("groups");

			// Build parameters for listing users
			Iterator userIterator = memberUsers.iterator();
			JSONArray users = new JSONArray();
			while (userIterator.hasNext()) {
				String id = (String) userIterator.next();
				JSONObject _user = new JSONObject();
				_user.put("memberId", id);
				_user.put("memberType", "user");
				users.add(_user);
			}

			// Build parameters for listing groups
			Iterator groupIterator = memberGroups.iterator();
			JSONArray groups = new JSONArray();
			while (groupIterator.hasNext()) {
				String id = (String) groupIterator.next();
				JSONObject _group = new JSONObject();
				_group.put("memberId", id);
				_group.put("memberType", "group");
				groups.add(_group);
			}

			// Concatenate users and groups
			JSONArray usersAndGroups = new JSONArray();
			usersAndGroups.addAll(users);
			usersAndGroups.addAll(groups);

			// Output for jTable
			JSONObject result = new JSONObject();
			result.put("Result", "OK");
			
			//sort
			if (parameters.get("jtSorting") != null){
				usersAndGroups = ApiUtil.sortList(usersAndGroups, parameters.get("jtSorting")[0]);
			}
			
			result.put("Records", usersAndGroups);
			setResult(result);
			return Action.SUCCESS;

		} catch (Exception e) {
			e.printStackTrace();
			// Output for jTable
			JSONObject result = new JSONObject();
			result.put("ERROR", e.toString());
			setResult(result);
			return Action.ERROR;
		}
	}

	/**
	 * 
	 * @param parameters
	 * @return
	 */
	private String executeAddMember(Map<String, String[]> parameters) {
		// Get the target group's id
		String id = extractId(parameters);
		if (id == null)
			return Action.ERROR;

		// Convert input parameters
		HashMap<String, Object> restParams = new HashMap<String, Object>();

		// Set modifier
		String currentUser;
		try {
			currentUser = getCredentials().getUser();
			restParams.put("modifier", currentUser);
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}

		// Build parameters for adding users/groups
		String memberId = extractParam(parameters, "memberId");
		String memberType = extractParam(parameters, "memberType");
		JSONArray users = new JSONArray();
		JSONArray groups = new JSONArray();
		if (memberType.equals("user")) {
			JSONObject user = new JSONObject();
			user.put("id", memberId);
			users.add(user);
			restParams.put("users", users.toString());
		} else if (memberType.equals("group")) {
			JSONObject group = new JSONObject();
			group.put("id", memberId);
			groups.add(group);
			restParams.put("groups", groups.toString());
		}

		// Call REST API
		JSONObject obj = new JSONObject();
		String urlString = BASEPATH + "add/" + id;
		try {
			obj = conn.putJSON(urlString, restParams);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
		}

		// Output for jTable
		JSONObject result = new JSONObject();
		if (obj.get("status").equals("success")) {
			result.put("Result", "OK");
			// Set the created user info for jTable
			JSONObject record = new JSONObject();

			if (memberType.equals("user")) {
				UserApiAction userApiAction = new UserApiAction();
				userApiAction.setConn(conn);
				JSONObject _user = userApiAction.getUser(memberId);
				record.put("memberId", _user.get("userId"));
				record.put("memberType", "user"); // Set memberType for jTable
			} else if (memberType.equals("group")) {
				JSONObject _group = getGroup(memberId);
				record.put("memberId", _group.get("groupId"));
				record.put("memberType", "group"); // Set memberType for jTable
			}
			// Set the added member info for jTable
			result.put("Record", record);

			setResult(result);
			return Action.SUCCESS;
		} else {
			result.put("Result", "ERROR");
			result.put("Message", getErrMsg(obj));
			return Action.ERROR;
		}

	}

	/**
	 * 
	 * @param parameters
	 * @return
	 */
	private String executeRemoveMember(Map<String, String[]> parameters) {
		// Get the target group's id
		String id = extractId(parameters);
		if (id == null)
			return Action.ERROR;

		// Convert input parameters
		HashMap<String, Object> restParams = new HashMap<String, Object>();

		// Set modifier
		String currentUser;
		try {
			currentUser = getCredentials().getUser();
			restParams.put("modifier", currentUser);
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}

		// Set member's id to be removed.
		String memberId = extractParam(parameters, "memberId");

		//TODO THIS IS WORKAROUND: jTable doesn't pass memberType on deleting an item.
		//TODO As long as user/group have unique id on the same DB, first search user, then group.
		//Discern whether the target member is user/group
		String memberType = "";
		JSONObject g = getGroup(id);
		List<String> memberUsers = (List<String>) g.get("users");
		List<String> memberGroups = (List<String>) g.get("groups");

		Iterator u_iterator = memberUsers.iterator();
		while (u_iterator.hasNext()) {
			if (memberId.equals(u_iterator.next())) {
				memberType = "user";
				break;
			}
		}

		if (!memberType.equals("user")) {
			Iterator g_iterator = memberGroups.iterator();
			while (g_iterator.hasNext()) {
				if (memberId.equals(g_iterator.next())) {
					memberType = "group";
					break;
				}
			}
		}

		// Build parameters for removing users/groups
		JSONArray users = new JSONArray();
		JSONArray groups = new JSONArray();
		if (memberType.equals("user")) {
			JSONObject user = new JSONObject();
			user.put("id", memberId);
			users.add(user);
			restParams.put("users", users.toString());
		} else if (memberType.equals("group")) {
			JSONObject group = new JSONObject();
			group.put("id", memberId);
			groups.add(group);
			restParams.put("groups", groups.toString());
		}

		// Call REST API
		JSONObject obj;

		obj = new JSONObject();
		String urlString = BASEPATH + "remove/" + id;
		try {
			obj = conn.putJSON(urlString, restParams);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
		}

		// Output for jTable
		JSONObject result = new JSONObject();
		if (obj.get("status").equals("success")) {
			result.put("Result", "OK");

			setResult(result);
			return Action.SUCCESS;
		} else {
			result.put("Result", "ERROR");
			result.put("Message", getErrMsg(obj));
			return Action.ERROR;
		}
		// if irregular/stale member id is registered, delete it successfully
		// without calling REST API

	}

	/**
	 * Get groups list JSON via REST API from Nemaki Server
	 * 
	 * @return
	 */
	private JSONArray getGroupList() {
		try {
			JSONObject obj = new JSONObject();
			String urlString = BASEPATH + "list";
			obj = conn.getJSON(urlString, new HashMap<String, Object>());
			if (obj.get("status").equals("success")) {
				JSONArray groups = (JSONArray) obj.get("groups");
				return groups;
			} else {
				return null;
			}
		} catch (ServerConnectionException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get a group JSON via REST API from Nemaki Server
	 * 
	 * @param id
	 * @return
	 */
	private JSONObject getGroup(String id) {
		JSONArray groups = getGroupList();
		if (groups == null)
			return null;

		Iterator<?> iterator = groups.iterator();
		while (iterator.hasNext()) {
			JSONObject group = (JSONObject) iterator.next();
			if (group.get("groupId").equals(id)) {
				return group;
			}
		}
		return null;
	}

	/**
	 * Utility
	 */
	private HashMap<String, Object> addAdminParams(
			HashMap<String, Object> params) {
		try {
			Credentials credentials = getCredentials();
			params.put("admin", credentials.getUser());
			params.put("adminpass", credentials.getPassword());
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}
		return params;
	}

	private String extractId(Map<String, String[]> parameters) {
		return extractParam(parameters, "groupId");
	}

	private String extractParam(Map<String, String[]> parameters, String key) {
		String id = parameters.get(key)[0];
		if (id != null && !id.equals("")) {
			return id;
		} else {
			return null;
		}
	}

	/**
	 * Convert a map of parameter for HttpRequest's Body, which requires
	 * Map<String,Object>. And at the same time, adjust some parameters.
	 * 
	 * @param parameters
	 * @return
	 */
	private HashMap<String, Object> buildParameters(
			Map<String, String[]> parameters) {
		// Remove unnecessary parameters
		if (parameters.get("logic") != null)
			parameters.remove("logic");
		if (parameters.get("groupId") != null)
			parameters.remove("groupId");

		// Build
		HashMap<String, Object> builtParams = new HashMap<String, Object>();
		for (String _key : parameters.keySet()) {
			String _val = parameters.get(_key)[0];
			// Mapping between input parameter and REST parameter
			if (_key.equals("groupName"))
				_key = "name";

			builtParams.put(_key, _val);
		}
		return builtParams;
	}

	private String getErrMsg(JSONObject obj) {
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
	public JSONObject getResult() {
		return result;
	}

	public void setResult(JSONObject result) {
		this.result = result;
	}

	@Override
	public void setParameters(Map<String, String[]> arg0) {
		this.parameters = arg0;
	}
}
