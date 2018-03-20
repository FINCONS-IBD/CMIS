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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import jp.aegif.struts2cmisexplorer.domain.Folder;
import jp.aegif.struts2cmisexplorer.domain.Node;
import jp.aegif.struts2cmisexplorer.domain.NodesListPage;
import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.domain.exceptions.NotLoggedInException;
import jp.aegif.struts2cmisexplorer.domain.exceptions.UnauthorizedException;
import jp.aegif.struts2cmisexplorer.opencmisbinding.OpenCMISRepositoryClientFacade;

import com.opensymphony.xwork2.ActionContext;

/**
 * Struts2 Action support bean to get the list of nodes from a particular CMIS folder.
 */
public class ShowFolderAction extends AuthenticatedAction {

	private static final long serialVersionUID = 4597510098838425791L;

	/**
	 * Identifier of the folder. For instance in Alfresco:
	 * workspace://SpacesStore/cd79b86c-3068-446f-bd76-61d895de7af1
	 */
	private String folder;

	/**
	 * Identifer of the root folder. We choice to set this folder to Site folder, to exclude the other system folders
	 */
	private String rootFolder;

	/**
	 * Identifier of the folder, by path.
	 * For instance in Alfresco: /Data Dictionary/Messages
	 */
	private String folderPath;

	/**
	 * Number of results that are skipped when displaying. It is used for paging
	 * results.
	 */
	private int skipCount = 0;

	/**
	 * Information about nodes that are to be displayed on the current page.
	 */
	private NodesListPage page;

	/**
	 * Struts2 execution.
	 */
	@Override
	public String execute() {
		try {
			if (folder == null) {

				//after the login (the unique case when the folder is null) 
				//skip the other folder and set the Sites folder as root folder
				getSiteFolderPath();
				folder = getFacade().getNodeRef(rootFolder);	
				ActionContext.getContext().getSession().put("folderId", folder);

			}
			//Set filer null, because an inadequate filter hinders getNodeListPage method   
			OperationContext context = getFacade().getSession().getDefaultContext();
			
			//by leo: set again the max item per page to config parameter
			int max = jp.aegif.struts2cmisexplorer.opencmisbinding.util.FacadeUtil.getMaxItemsPerPage();
			
			context.setMaxItemsPerPage(max);
			context.setFilter(null);
			
			page = getFacade().getNodesListPage(folder,
					skipCount);
		} catch (UnauthorizedException e) {
			return "unauthorized";
		} catch (ConnectionFailedException e) {
			return LOGIN;
		//re-login for any other exception as well(for example, getFacade() fails with the server restarted) 
		} catch (Exception e){
			return LOGIN;
		}
		return SUCCESS;
	}

	private void getSiteFolderPath() throws ConnectionFailedException {
		OpenCMISRepositoryClientFacade facade = getFacade();

		String statement = "SELECT cmis:path FROM cmis:folder where cmis:objectTypeId='F:st:sites'";
		
		ItemIterable<QueryResult> results = facade.getSession().query(statement,false);
		for (Iterator<QueryResult> it = results.iterator(); it.hasNext();) {
			QueryResult r = it.next();
			
			rootFolder = String.valueOf(r.getPropertyById(PropertyIds.PATH).getFirstValue());
		}
	}
	
	/**
	 * The "skip count" to be used if the user clicks on "Previous".
	 */
	public int getPreviousSkipCount() throws NotLoggedInException {
		int previousSkipCount = skipCount
				- getFacade().getMaxItemsPerPage();
		if (previousSkipCount < 0) {
			previousSkipCount = 0;
		}
		return previousSkipCount;
	}

	/**
	 * The "skip count" to be used if the user clicks on "Next".
	 */
	public int getNextSkipCount() throws NotLoggedInException {
		return skipCount + getFacade().getMaxItemsPerPage();
	}

	/**
	 * Whether the "Previous" button is needed.
	 */
	public boolean getShowPrevious() throws NotLoggedInException {
		return skipCount > 0;
	}

	/**
	 * Whether the "Next" button is needed.
	 */
	public boolean getShowNext() throws NotLoggedInException {
		return skipCount + getFacade().getMaxItemsPerPage() < page
				.getTotalNumberOfNodes();
	}

	//Breadcrumb(Folder tree, in fact) for the current folder
	public List<Map<String,String>> setBreadcrumb(){
		//return ActionUtil.setBreadcrumb(folder);
		return buildParentsList(folder);
	}
	
	/**
	 * Getters / Setters
	 */
	public List<Node> getNodes() {
		return page.getNodes();
	}

	public long getTotalNumberOfNodes() {
		return page.getTotalNumberOfNodes();
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}
	
	public String getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

}