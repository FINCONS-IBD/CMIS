package jp.aegif.struts2cmisexplorer.domain;

import java.util.HashMap;
import java.util.Map;

public class Folder extends Node {

	@Override
	public String toString() {
		@SuppressWarnings("serial")
		Map<String, Object> m = new HashMap<String, Object>() {
			{
				put("name", getName());
				put("type", getType());
				put("parentId", getParent());
				put("contentId", getId());
				put("acl", getAcl());
				put("path", getPath());
				put("createdBy", getCreatedBy());
				put("creationDate", getCreationDate());
				put("lastModifiedBy", getLastModifiedBy());
				put("lastModificationDate", getLastModificationDate());
			}
		};
		return m.toString();
	}
}
