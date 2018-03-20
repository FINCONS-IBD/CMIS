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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.aegif.struts2cmisexplorer.domain.Aspect;
import jp.aegif.struts2cmisexplorer.domain.Credentials;
import jp.aegif.struts2cmisexplorer.domain.RepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.opencmisbinding.OpenCMISRepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.struts2actions.base.FolderActionBase;
import jp.aegif.struts2cmisexplorer.struts2actions.util.AspectUtil;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.util.CreateIfNull;
import com.opensymphony.xwork2.util.Element;

public class UpdateFolderAction extends FolderActionBase {

	private static final long serialVersionUID = -6023953202240961283L;
	private Log logger = LogFactory.getLog(UpdateFolderAction.class);

	public List<Aspect> modifiedAspects;
	private List<String> aspectDelChks;
	private List<String> aspectModChks;
	private List<Aspect> docAspects;
	private List<Aspect> otherAspects;
	
	@Override
	public String execute() {
		logger.debug("#execute: " + getModel());
		if (getModel().getParent() == null) {
			logger.error("parentId must not be null!");
			return ERROR;
		}
		String logic = getLogic();
		try {
			if ("add".equals(logic)) {
				executeAdd();
			} else if ("update".equals(logic)) {
				executeUpdate();
			} else if ("delete".equals(logic)) {
				executeDelete();
			} else {
				throw new Exception("logic is not specified.");
			}
		} catch (Exception e) {
			logger.error(e);
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * @throws Exception
	 * 
	 */
	private void executeDelete() throws Exception {
		try {
			RepositoryClientFacade facade = getFacade();
			Folder folder = (Folder) facade.getSession().getObject(
					getModel().getId());
			folder.delete(true);
			logger.debug("Folder deleted: " + folder.getId());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * @throws Exception
	 * 
	 */
	private void executeUpdate() throws Exception {
		try {
			Credentials c = getCredentials();
			RepositoryClientFacade facade = new OpenCMISRepositoryClientFacade(
					c.getUser(), c.getPassword());
			Folder folder = (Folder) facade.getSession().getObject(
					getModel().getId());
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(PropertyIds.NAME, getModel().getName());
			logger.debug("Will update: " + folder.getId());
			
			//Prepare parameters for updateProperties method
			Session session = facade.getSession();
			String repositoryId = session.getRepositoryInfo().getId();
			Holder<String> objectHolder = new Holder<String>(getModel().getId());
			Set<Updatability> updatebility = new HashSet<Updatability>();
	        updatebility.add(Updatability.READWRITE);
			PropertiesImpl properties = (PropertiesImpl) session.getObjectFactory().convertProperties(props, folder.getType(), null, updatebility);
						
			//Build an aspect's extension based upon the input 
			List<CmisExtensionElement> docExts = folder.getExtensions(ExtensionLevel.OBJECT);
			
			properties.setExtensions(AspectUtil.buildExtensions
					(modifiedAspects, docExts,aspectDelChks, aspectModChks));
			
			//Update method
			facade.getSession().getBinding().getObjectService().updateProperties(
					repositoryId, objectHolder, null, properties, null);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * @throws Exception
	 * 
	 */
	private void executeAdd() throws Exception {
		try {
			Credentials c = getCredentials();
			RepositoryClientFacade facade = new OpenCMISRepositoryClientFacade(
					c.getUser(), c.getPassword());
			Folder parent = facade.getFolder(getModel().getParent());
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(PropertyIds.OBJECT_TYPE_ID, getModel().getType());
			props.put(PropertyIds.NAME, getModel().getName());
			props.put(PropertyIds.PATH, buildPath(getModel().getParent(),getModel().getName() ));	//setting path
			Folder newFolder = parent.createFolder(props);
			logger.debug("New folder created: " + newFolder.getId());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	@Override
	public void validate(){
		//name is required(except when deleting)
		String logic = getLogic();
		if(logic.equals("delete")) return;
		
		String name = getModel().getName(); 
		if(name == null || name.equals("")){
			addActionError(getText("err_noname"));
		}
		return;
	}
	
	/**
	 * 
	 */
	public List<Aspect> getModifiedAspects() {
		return modifiedAspects;
	}
	
	@Element(value = jp.aegif.struts2cmisexplorer.domain.Aspect.class)
	@CreateIfNull( value = true )
	public void setModifiedAspects(List<Aspect> modifiedAspects) {
		this.modifiedAspects = modifiedAspects;
	}
	
	public List<String> getAspectDelChks() {
		return aspectDelChks;
	}

	@Element(value = java.lang.String.class)
	@CreateIfNull( value = true )
	public void setAspectDelChks(List<String> aspectDelChks) {
		this.aspectDelChks = aspectDelChks;
	}
	
	public List<String> getAspectModChks() {
		return aspectModChks;
	}
	
	@Element(value = java.lang.String.class)
	@CreateIfNull( value = true )
	public void setAspectModChks(List<String> aspectModChks) {
		this.aspectModChks = aspectModChks;
	}

	public List<Aspect> getDocAspects() {
		return docAspects;
	}

	public void setDocAspects(List<Aspect> docAspects) {
		this.docAspects = docAspects;
	}

	public List<Aspect> getOtherAspects() {
		return otherAspects;
	}

	public void setOtherAspects(List<Aspect> otherAspects) {
		this.otherAspects = otherAspects;
	}

}