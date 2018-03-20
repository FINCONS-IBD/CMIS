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
import jp.aegif.struts2cmisexplorer.domain.exceptions.NotLoggedInException;
import jp.aegif.struts2cmisexplorer.struts2actions.base.FolderActionBase;
import jp.aegif.struts2cmisexplorer.struts2actions.util.AspectUtil;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.runtime.FolderImpl;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;

public class ShowFolderDetailsAction extends FolderActionBase {

	private static final long serialVersionUID = -1467744015769465948L;
	private static final Log logger = LogFactory
			.getLog(ShowFolderDetailsAction.class);

	private List<Aspect> otherAspects;
	
	public List<Aspect> getOtherAspects() {
		return otherAspects;
	}

	public void setOtherAspects(List<Aspect> otherAspects) {
		this.otherAspects = otherAspects;
	}

	
	@Override
	public String execute() {
		try {
			try {
				getCredentials();
			} catch (NotLoggedInException e) {
				return LOGIN;
			}
			RepositoryClientFacade facade = getFacade();
			if(facade == null) return ERROR;
			CmisObject content = facade.getSession().getObject(
					getModel().getId());
			
			if (content instanceof FolderImpl) {
				FolderImpl f = (FolderImpl) content;
				getModel().setAcl(f.getAcl().getAces());
				getModel().setName(f.getName());
				getModel().setType("cmis:folder");
				getModel().setCreatedBy(f.getCreatedBy());
				getModel().setCreationDate(f.getCreationDate());
				getModel().setLastModifiedBy(f.getLastModifiedBy());
				getModel().setLastModificationDate(f.getLastModificationDate());
				getModel().setPath(f.getPath());
				
				//set aspects
//				List<CmisExtensionElement> extElems = content.getExtensions(
//						ExtensionLevel.OBJECT);
//				List<Aspect> nemakiAspects = AspectUtil.getNemakiAspects();
//				List<Aspect> docAspects = AspectUtil.getAspects(extElems, nemakiAspects);
//				List<Aspect> otherAspects = AspectUtil.getOtherAspects(docAspects, nemakiAspects);
				//sort
//				docAspects = AspectUtil.sortAspects(docAspects);
//				otherAspects = AspectUtil.sortAspects(otherAspects);
				//register content's own aspects and Nemaki aspects
//				getModel().setAspects(docAspects);
//				this.setOtherAspects(otherAspects);
				
				try {
					//register parent folder
					String folder = getFacade().getNodeRef("/");
					ActionContext.getContext().getSession().put("folderId", folder);
					
					//Get NodeListPage
					NodesListPage page = getFacade().getNodesListPage(folder,
							0);
					
				} catch (ConnectionFailedException e) {
					e.printStackTrace();
				}
			} else {
				return ERROR;
			}
			if (logger.isDebugEnabled())
				logger.debug("model: " + getModel());
		} catch (ConnectionFailedException e) {
			logger.error(e);
			return ERROR;
		}
		return SUCCESS;
	}

}
