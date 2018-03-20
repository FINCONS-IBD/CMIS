package jp.aegif.struts2cmisexplorer.domain;

import java.util.HashMap;
import java.util.Map;

public class User {

	private String id;
	private String name;
	private String lastName;
	private String firstName;

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getId() {
		return id;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public String getName() {
		return name;
	}

	public String getLastName() {
		return lastName;
	}

	public User() {

	}

	public User(String name, String lastName, String firstName) {
		this.name = name;
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		@SuppressWarnings("serial")
		Map<String, String> map = new HashMap<String, String>() {
			{
				put("name", name);
				put("lastName", lastName);
			}
		};
		return map.toString();
	}
}