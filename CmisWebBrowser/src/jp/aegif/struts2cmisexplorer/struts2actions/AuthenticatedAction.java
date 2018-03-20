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
package jp.aegif.struts2cmisexplorer.struts2actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import jp.aegif.struts2cmisexplorer.domain.Credentials;
import jp.aegif.struts2cmisexplorer.domain.Document;
import jp.aegif.struts2cmisexplorer.domain.Folder;
import jp.aegif.struts2cmisexplorer.domain.Node;
import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.domain.exceptions.NotLoggedInException;
import jp.aegif.struts2cmisexplorer.opencmisbinding.OpenCMISRepositoryClientFacade;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlPrincipalDataImpl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Parent class for Struts2 Action support beans that need user and password.
 */
public class AuthenticatedAction extends ActionSupport {

	private static final long serialVersionUID = 4132296228128594877L;
	protected String title = "Struts2CmisExplorer";
	private String logic;

	/**
	 * Get user and password.
	 */
	public Credentials getCredentials() throws NotLoggedInException {
		Map<String, Object> session = ActionContext.getContext().getSession();
		String user = (String) session.get("user");
		String password = (String) session.get("password");
		if (user == null || password == null) {
			throw new NotLoggedInException();
		}
		return new Credentials(user, password);
	}

	public OpenCMISRepositoryClientFacade getFacade() {
		return (OpenCMISRepositoryClientFacade) ActionContext.getContext()
				.getSession().get("facade");
	}

	/**
	 * Build List of <id,name> tuple for node's ancestors by descending order 
	 * @param folder
	 * @return
	 */
	public List<Map<String, String>> buildParentsList(String folder) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		OpenCMISRepositoryClientFacade facade = getFacade();

		try {
			org.apache.chemistry.opencmis.client.api.Folder child;
			String id = folder;
			String siteFolder = getSiteFolderPath();
			//skip the root repository link and begin the nav Breadcrumb from Site folder
			
			while (id != null ) {
				child = facade.getFolder(id);
				if (child == null)
					break;
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", id);
				map.put("name", child.getName());
				list.add(map);
				org.apache.chemistry.opencmis.client.api.Folder parent = child
						.getFolderParent();
			
				if (parent == null || id.equals(siteFolder))
					break;
				
				id = parent.getId();
			}
		} catch (ConnectionFailedException e) {
			e.printStackTrace();
		}
		Collections.reverse(list);
		return list;
	}
	
	private String getSiteFolderPath() throws ConnectionFailedException {
		OpenCMISRepositoryClientFacade facade = getFacade();

		String statement = "SELECT cmis:objectId FROM cmis:folder where cmis:objectTypeId='F:st:sites'";
		String rootFolder = null;
		ItemIterable<QueryResult> results = facade.getSession().query(statement,false);
		for (Iterator<QueryResult> it = results.iterator(); it.hasNext();) {
			QueryResult r = it.next();
			
			rootFolder = String.valueOf(r.getPropertyById(PropertyIds.OBJECT_ID).getFirstValue());
		}
		return rootFolder;
	}

	/**
	 * Build full path
	 * @param parentId
	 * @param node
	 * @return
	 */
	public String buildPath(String parentId, String node) {
		String path = new String();
		StringBuilder sb = new StringBuilder();

		List<Map<String, String>> breadcrumb = buildParentsList(parentId);
		if (breadcrumb == null)
			return "";

		for (int i = 0; i < breadcrumb.size(); i++) {
			String _name = breadcrumb.get(i).get("name");
			if (i == 0) {
				sb.append("/");
			} else {
				sb.append(_name + "/");
			}
		}

		sb.append(node);
		path = sb.toString();
		return path;
	}

	/**
	 * 
	 * @param principalId
	 * @param permission
	 * @return
	 */
	public Ace createAce(String principalId, String permission) {
		AccessControlEntryImpl ace = new AccessControlEntryImpl(
				new AccessControlPrincipalDataImpl(principalId),
				Arrays.asList(new String[] { permission }));
		return ace;
	}

	/**
	 * Get page title
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

}
