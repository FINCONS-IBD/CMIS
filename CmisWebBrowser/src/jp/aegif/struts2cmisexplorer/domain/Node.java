/***************************************************************************************
 * Copyright (c) 2010 Aegif  - http://aegif.jp                                          *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation; either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 ****************************************************************************************/
package jp.aegif.struts2cmisexplorer.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.opencmisbinding.OpenCMISRepositoryClientFacade;

import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A Node is a file or a folder of the repository.
 */
public class Node {

	/**
	 * Constructor.
	 */
	public Node() {
	}

	public Node(String id, String name, boolean folder) {
		this.id = id;
		this.name = name;
		this.folder = folder;
	}

	/**
	 * Identifier of this node.
	 */
	private String id;

	/**
	 * Human-readable name of this node.
	 */
	private String name;

	/**
	 * Whether this node is a folder or not.
	 */
	private boolean folder;

	/**
	 * Mimetype of this node
	 */
	private String mimetype;

	/**
	 * CreationDate / LastModificationDate
	 */
	private GregorianCalendar creationDate, lastModificationDate;

	/**
	 * Creator / Modificator
	 */
	private String createdBy, lastModifiedBy;

	/**
	 * Size of this node
	 */
	private long size;

	/**
	 * Type of this node
	 */
	private String type;

	/**
	 * Parent id of this node
	 */
	private String parent;

	/**
	 * ACL of this node
	 */
	private List<Ace> acl;

	/**
	 * Path of this node
	 */
	private String path;

	/**
	 * Aspects of this node
	 */
	private List<Aspect> aspects = new ArrayList<Aspect>();

	/**
	 * Extension elements of this node
	 */
	private List<CmisExtensionElement> extensions = new ArrayList<CmisExtensionElement>();

	private boolean isOwner;
	private boolean isCollaborator;
	private boolean isConsumer;

	/**
	 * Getters / Setters.
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isFolder() {
		return folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public GregorianCalendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(GregorianCalendar creationDate) {
		this.creationDate = creationDate;
	}

	public GregorianCalendar getLastModificationDate() {
		return lastModificationDate;
	}

	public void setLastModificationDate(GregorianCalendar lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public boolean isCollaborator() {
		return isCollaborator;
	}

	public void setCollaborator(boolean isCollaborator) {
		this.isCollaborator = isCollaborator;
	}

	public boolean isConsumer() {
		return isConsumer;
	}

	public void setConsumer(boolean isConsumer) {
		this.isConsumer = isConsumer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<Ace> getAcl() {
		return acl;
	}

	public void setAcl(List<Ace> acl) {
		this.acl = acl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Aspect> getAspects() {
		return aspects;
	}

	public void setAspects(List<Aspect> aspects) {
		this.aspects = aspects;
	}

	public List<CmisExtensionElement> getExtensions() {
		return extensions;
	}

	public void setExtensions(List<CmisExtensionElement> extensions) {
		this.extensions = extensions;
	}

	
	//TODO 削除する
	@SuppressWarnings("unchecked")
	public JSONArray getAclAsJson() {
		JSONArray jarray = new JSONArray();
		for (Iterator<Ace> it = acl.iterator(); it.hasNext();) {
			Ace ace = it.next();
			JSONObject json = new JSONObject();
			json.put("name", ace.getPrincipalId());
			json.put("permission", ace.getPermissions().get(0));
			jarray.add(json);
		}
		return jarray;
	}

	protected Map<String, Object> getMap() {
		Map<String, Object> m = new HashMap<String, Object>() {
			private static final long serialVersionUID = 7655555377930189809L;
			{
				put("name", getName());
				put("type", getType());
				put("parentId", getParent());
				put("contentId", getId());
				put("acl", getAcl());
				put("createdBy", getCreatedBy());
				put("creationDate", getCreationDate());
				put("lastModifiedBy", getLastModifiedBy());
				put("lastModificationDate", getLastModificationDate());
			}
		};
		return m;
	}

	//Utility
	public String convertToDate(GregorianCalendar cal){
		Date date = cal.getTime();
		DateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zz");
		return df.format(date);
	}
	
	public String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}