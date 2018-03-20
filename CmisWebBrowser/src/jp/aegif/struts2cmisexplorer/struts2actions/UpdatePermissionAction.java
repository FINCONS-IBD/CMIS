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
import java.util.List;

import jp.aegif.struts2cmisexplorer.domain.RepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.struts2actions.base.DocumentActionBase;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdatePermissionAction extends DocumentActionBase {

	private static final long serialVersionUID = -3779823800045674970L;
	private Log logger = LogFactory.getLog(UpdatePermissionAction.class);

	private String addPermissionString;
	private String delPermissionString;
	private String folder;
	private String folderPath;
	
	public void setDelPermissionString(String delPermissionString) {
		this.delPermissionString = delPermissionString;
	}

	public void setAddPermissionString(String addPermissionString) {
		this.addPermissionString = addPermissionString;
	}
	
	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	/**
	 * 
	 */
	@Override
	public String execute() {
		logger.debug("#execute: " + getModel());
		if (getModel().getParent() == null) {
			logger.error("parent must not be null!");
			return ERROR;
		}
		try {
			if ("permission".equals(getLogic())) {
				logger.debug("addPermissionString: " + addPermissionString);
				logger.debug("delPermissionString: " + delPermissionString);
				executePermission();
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
	 *             everyone;cmis:read;;jiro;cmis:all;;
	 */
	private void executePermission() throws Exception {
		logger.debug("#executePermission");
		try {
			RepositoryClientFacade facade = getFacade();
			CmisObject content = facade.getSession().getObject(
					getModel().getId());
			logger.debug("Will update permission: " + content.getId());

			
			
			//To modify existing Acl, delete it and add new Acl base upon input data.
			//Otherwise an Acl has multiple values.
			List<Ace> delAce = convertStringToAces(delPermissionString);
			Acl acl = content.getAcl();
			List<Ace> aces = acl.getAces();
			for(Ace ace : aces){
				String id = ace.getPrincipalId();
				List<String> permissions = ace.getPermissions();
				List<Ace> newAces = convertStringToAces(addPermissionString);
				for(Ace newAce : newAces){
					if (newAce.getPrincipalId().equals(id)){
						if(!newAce.getPermissions().get(0).equals(permissions.get(0))){
							delAce.add(ace);
						}
					}
				}
			}

			content.applyAcl(convertStringToAces(addPermissionString),
					delAce,
					AclPropagation.OBJECTONLY);
			
			logger.debug("Content permission udpated: " + content.getId()
					+ ", " + content.getAcl().getAces());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 
	 * @param permissionString
	 * @return
	 */
	private List<Ace> convertStringToAces(String permissionString) {
		if (StringUtils.isEmpty(permissionString))
			return new ArrayList<Ace>();
		String[] permissions = permissionString.split(";;");
		List<Ace> aces = new ArrayList<Ace>();
		for (String perm : permissions) {
			if (perm == null)
				continue;
			String[] onePerm = perm.split(";");
			aces.add(createAce(onePerm[0], onePerm[1]));
		}
		logger.debug("aces: " + aces);
		return aces;
	}
}