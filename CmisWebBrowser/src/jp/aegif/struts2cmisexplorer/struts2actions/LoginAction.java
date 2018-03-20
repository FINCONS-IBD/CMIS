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

import java.util.Map;

import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.domain.exceptions.UnauthorizedException;
import jp.aegif.struts2cmisexplorer.opencmisbinding.OpenCMISRepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.struts2actions.api.UserApiAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.tachibanakikaku.serverconnection.ServerConnection;

/**
 * Struts2 Action support bean for login. This is just a mock of authentication.
 * CMIS is stateless, so we just store the username/password and send them with
 * each request.
 */
public class LoginAction extends AuthenticatedAction {

	private static final long serialVersionUID = -8621501151940536209L;
	private static final Log log = LogFactory.getLog(LoginAction.class);

	/**
	 * Username of this session's user.
	 */
	private String user;

	/**
	 * Password of this session's user.
	 */
	private String password;

	/**
	 * FolderPath after Login
	 */
	private String folderPath;

	/**
	 * Struts2 execution.
	 */
	@Override
	public String execute() throws Exception {
		log.debug("#execute");
		
		try {
			// Session for server.
			// If this line was executed without exception, it means user/password is correct.
			OpenCMISRepositoryClientFacade facade = new OpenCMISRepositoryClientFacade(user, password);
			facade.getSession();
			
			// Session for client session.
			Map<String, Object> session = ActionContext.getContext().getSession();
			session.put("user", user);
			//session.put("user", getUserName(user));
			session.put("password", password);
			session.put("logged-in", "true");
			session.put("facade", facade);
			return SUCCESS;
		} catch (UnauthorizedException e) {
			addActionError(getText("login_failed"));
			return ERROR;
		} catch (ConnectionFailedException e) {
			addActionError(getText("connection_failed"));
			return ERROR;
		}
	}

	/**
	 * Whether the user is currently logged in or not.
	 */
	public boolean getLoggedIn() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		String loggedIn = (String) session.get("logged-in");
		return loggedIn != null && loggedIn.equals("true");
	}

	
	//TODO Use this method to show the name of user instead of id? 
	private String getUserName(String id){
		UserApiAction userApiAction = new UserApiAction();
		ServerConnection conn;
		try {
			ActionSupport actionSupport = new ActionSupport();
			String protocol = actionSupport.getText("nemaki.protocol");
			String host = actionSupport.getText("nemaki.host");
			Integer port = Integer.parseInt(actionSupport.getText("nemaki.port")); 
			
			conn = ServerConnection.createServerConnection(protocol,host, port);
			userApiAction.setConn(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		JSONObject user = userApiAction.getUser(id);
		try{
			return (String)user.get("userName");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Getters / Setters
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
}