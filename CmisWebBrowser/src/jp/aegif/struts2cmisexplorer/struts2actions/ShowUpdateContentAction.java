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
import java.util.Iterator;
import java.util.List;

import jp.aegif.struts2cmisexplorer.domain.Group;
import jp.aegif.struts2cmisexplorer.domain.RepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.domain.User;
import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.struts2actions.base.DocumentActionBase;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

public class ShowUpdateContentAction extends DocumentActionBase {

	private static final long serialVersionUID = 6646067169963433948L;
	private String updateTitle;
	private JSONObject usersAndGroups = new JSONObject();

	List<User> users = new ArrayList<User>();
	List<Group> groups = new ArrayList<Group>();

	private static final Log logger = LogFactory
			.getLog(ShowUpdateContentAction.class);

	public void setUpdateTitle(String updateTitle) {
		this.updateTitle = updateTitle;
	}

	public JSONObject getUserGroup() {
		return usersAndGroups;
	}

	@Override
	public String execute() {
		if ("permission".equals(getLogic())) {
			try {
				RepositoryClientFacade facade = getFacade();
				CmisObject content = facade.getSession().getObject(
						getModel().getId());
				getModel().setAcl(content.getAcl().getAces());

				initializeUserBean(facade);
				initializeGroupBean(facade);
				initializeUserGroupJson();

				if (logger.isDebugEnabled()) {
					logger.debug("model: " + getModel());
					logger.debug("users: " + users);
					logger.debug("groups: " + groups);
					logger.debug("return permission");
					
				}
			} catch (ConnectionFailedException e) {
				logger.error(e);
			}
			this.title = this.updateTitle;
			return "permission";
		} else {
			logger.debug("return error: " + getLogic());
			return ERROR;
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeUserGroupJson() {
		for (int i = 0; i < users.size(); i++) {
			User u = users.get(i);
			//usersAndGroups.put(u.getName(), u.getName());
			usersAndGroups.put(u.getId(), u.getId());	//fix for ID
		}
		for (int i = 0; i < groups.size(); i++) {
			Group g = groups.get(i);
			usersAndGroups.put(g.getId(), g.getId());
		}
	}

	private void initializeUserBean(RepositoryClientFacade facade)
			throws ConnectionFailedException {
		if (users.size() > 0)
			return;
		OperationContext context = facade.getSession().getDefaultContext();
		context.setFilterString("type:user");
		ItemIterable<QueryResult> results = facade.getSession().query(
				"SELECT * FROM aegif:user", false);

		for (Iterator<QueryResult> it = results.iterator(); it.hasNext();) {
			QueryResult r = it.next();
			User u = new User();
			u.setFirstName(String.valueOf(r.getPropertyById("firstName")
					.getFirstValue()));
			u.setLastName(String.valueOf(r.getPropertyById("lastName")
					.getFirstValue()));
			u.setName(String.valueOf(r.getPropertyById("name").getFirstValue()));
			u.setId(String.valueOf(r.getPropertyById("id").getFirstValue()));	//fix for ID
			users.add(u);
		}
	}

	private void initializeGroupBean(RepositoryClientFacade facade)
			throws ConnectionFailedException {
		if (groups.size() > 0)
			return;
		// dummy query
		ItemIterable<QueryResult> results = facade.getSession().query(
				"SELECT * FROM aegif:group", false);

		for (Iterator<QueryResult> it = results.iterator(); it.hasNext();) {
			QueryResult r = it.next();
			Group g = new Group();
			g.setId(String.valueOf(r.getPropertyById("id").getFirstValue()));
			groups.add(g);
		}
	}
}