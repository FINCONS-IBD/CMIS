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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;

/**
 * Struts2 Action support bean for logout.
 */
public class LogoutAction extends AuthenticatedAction {

	private static final long serialVersionUID = -4986031143153780657L;
	private static final Log log = LogFactory.getLog(LogoutAction.class);

	/**
	 * Struts2 execution.
	 */
	@Override
	public String execute() throws Exception {
		// remove client session
		Map<String, Object> session = ActionContext.getContext().getSession();
		session.remove("logged-in");
		session.remove("user");
		session.remove("password");

		// remove server session
		session.remove("facade");
		
		log.debug("logout");
		return SUCCESS;
	}
}