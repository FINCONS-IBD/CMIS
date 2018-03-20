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

import java.util.List;

import jp.aegif.struts2cmisexplorer.domain.Aspect;
import jp.aegif.struts2cmisexplorer.domain.NodesListPage;
import jp.aegif.struts2cmisexplorer.domain.RepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.struts2actions.base.FolderActionBase;
import jp.aegif.struts2cmisexplorer.struts2actions.util.AspectUtil;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;

public class ShowUpdateFolderAction extends FolderActionBase {

	private static final long serialVersionUID = 5474357849835208227L;
	private String createTitle;
	private String updateTitle;
	private String deleteTitle;
	
	private List<Aspect> otherAspects;
	
	private static final Log logger = LogFactory
			.getLog(ShowUpdateFolderAction.class);

	public void setCreateTitle(String createTitle) {
		this.createTitle = createTitle;
	}

	public void setUpdateTitle(String updateTitle) {
		this.updateTitle = updateTitle;
	}

	public void setDeleteTitle(String deleteTitle) {
		this.deleteTitle = deleteTitle;
	}

	@Override
	public String execute() {
		getModel().setType("cmis:folder");
		String logic = getLogic();
		if ("add".equals(logic)) {
			logger.debug("return create");
			this.title = this.createTitle;
			return "create";
		} else if ("update".equals(logic)) {
			logger.debug("return update");
			this.title = this.updateTitle;
			
			//set aspects
			RepositoryClientFacade facade = getFacade();
			if(facade == null) return ERROR;
			try {
				CmisObject content = facade.getSession().getObject(
						getModel().getId());
//				List<CmisExtensionElement> extElems = content.getExtensions(
//						ExtensionLevel.OBJECT);

//				List<Aspect> nemakiAspects = AspectUtil.getNemakiAspects();
//				List<Aspect> docAspects = AspectUtil.getAspects(extElems, nemakiAspects);
//				List<Aspect> otherAspects = AspectUtil.getOtherAspects(docAspects, nemakiAspects);
//				//sort
//				docAspects = AspectUtil.sortAspects(docAspects);
//				otherAspects = AspectUtil.sortAspects(otherAspects);
//				
//				//Register to ValueStack
//				getModel().setAspects(docAspects);
//				this.setOtherAspects(otherAspects);
				
				String folder = getFacade().getNodeRef("/");
				ActionContext.getContext().getSession().put("folderId", folder);
				
				NodesListPage page = getFacade().getNodesListPage(folder,
						0);
			} catch (ConnectionFailedException e) {
				e.printStackTrace();
			}
			return "update";
		} else if ("delete".equals(logic)) {
			logger.debug("return delete");
			this.title = this.deleteTitle;
			return "delete";
		} else {
			logger.debug("return error: " + logic);
			return ERROR;
		}
	}

	
	/**
	 * Getter & Setter
	 */
	public List<Aspect> getOtherAspects() {
		return otherAspects;
	}

	public void setOtherAspects(List<Aspect> otherAspects) {
		this.otherAspects = otherAspects;
	}
}