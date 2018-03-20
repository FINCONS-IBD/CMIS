package jp.aegif.struts2cmisexplorer.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Group {

	private String id;
	private List<User> users;
	private List<Group> groups;

	public Group() {

	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Group(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		@SuppressWarnings("serial")
		Map<String, Object> map = new HashMap<String, Object>() {
			{
				put("id", id);
				put("users", users);
				put("groups", groups);
			}
		};
		return map.toString();
	}
}