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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;

import jp.aegif.struts2cmisexplorer.domain.Aspect;
import jp.aegif.struts2cmisexplorer.domain.Credentials;
import jp.aegif.struts2cmisexplorer.domain.RepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.opencmisbinding.OpenCMISRepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.struts2actions.base.DocumentActionBase;
import jp.aegif.struts2cmisexplorer.struts2actions.util.AspectUtil;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.util.CreateIfNull;
import com.opensymphony.xwork2.util.Element;

@Conversion()
public class UpdateDocumentAction extends DocumentActionBase{

	private static final long serialVersionUID = 6646067169963433948L;
	private Log logger = LogFactory.getLog(UpdateDocumentAction.class);
	
	private List<Aspect> modifiedAspects;
	private List<String> aspectDelChks;
	private List<String> aspectModChks;
	private List<Aspect> docAspects;
	private List<Aspect> otherAspects;
	
	private String uploadContentType;
	private String uploadFileName;
	
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
		InputStream fis = null;
		InputStream stream = null;
		try {
			RepositoryClientFacade facade = getFacade();
			Document doc = (Document) facade.getSession().getObject(
					getModel().getId());
			doc.delete(true);
			logger.debug("document deleted: " + doc.getId());
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				throw new Exception(e);
			}
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				throw new Exception(e);
			}
		}
	}

	/**
	 * 
	 * @param document
	 * @throws Exception
	 */
	private void setAttachment(Document document) throws Exception {
		logger.debug("#setAttachment: " + getModel());
		InputStream fis = null;
		InputStream stream = null;
		try {
			byte content[] = new byte[(int) getModel().getUpload().length()];
			fis = new FileInputStream(getModel().getUpload());
			fis.read(content);
								
			stream = new ByteArrayInputStream(content);

			ContentStream contentStream = new ContentStreamImpl(uploadFileName,
					BigInteger.valueOf(content.length), uploadContentType, stream);
			
			document.setContentStream(contentStream, true);
			
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				throw new Exception(e);
			}
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				throw new Exception(e);
			}
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
			Document doc = (Document) facade.getSession().getObject(
					getModel().getId());
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(PropertyIds.NAME, getModel().getName());

			logger.debug("Will update: " + doc.getId());
			if (getModel().getUpload() != null)
				setAttachment(doc);
			
			//Prepare parameters for updateProperties method
			Session session = facade.getSession();
			String repositoryId = session.getRepositoryInfo().getId();
			Holder<String> objectHolder = new Holder<String>(getModel().getId());
			Set<Updatability> updatebility = new HashSet<Updatability>();
	        updatebility.add(Updatability.READWRITE);
			PropertiesImpl properties = (PropertiesImpl) session.getObjectFactory().convertProperties(props, doc.getType(), null, updatebility);
						
			//Build an aspect's extension based upon the input 
//			List<CmisExtensionElement> docExts = doc.getExtensions(ExtensionLevel.OBJECT);
			
//			properties.setExtensions(AspectUtil.buildExtensions
//					(modifiedAspects, docExts,aspectDelChks, aspectModChks));
			
			//Update method
			facade.getSession().getBinding().getObjectService().updateProperties(
					repositoryId, objectHolder, null, properties, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	/**
	 * @throws Exception
	 * 
	 */
	private void executeAdd() throws Exception {
		InputStream fis = null;
		InputStream stream = null;
		try {
			Credentials c = getCredentials();
			RepositoryClientFacade facade = new OpenCMISRepositoryClientFacade(
					c.getUser(), c.getPassword());
			Folder parent = facade.getFolder(getModel().getParent());
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			props.put(PropertyIds.NAME, getModel().getName());
			MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
			String mimeType = mimeTypesMap.getContentType(getModel().getUpload());
			props.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, mimeType);

//			props.put(PropertyIds.PATH, buildPath(getModel().getParent(), getModel().getName()));	//setting path
			
			byte content[] = new byte[(int) getModel().getUpload().length()];
			fis = new FileInputStream(getModel().getUpload());
			fis.read(content);
			stream = new ByteArrayInputStream(content);
			
			ContentStream contentStream = new ContentStreamImpl("content",
					BigInteger.valueOf(content.length),
					uploadContentType, stream);
			// List<Ace> addAces = Arrays.asList(new Ace[] { createAce(
			// (String) ActionContext.getContext().getSession()
			// .get("user"), BasicPermissions.ALL) });
			Document newDoc = parent.createDocument(props, contentStream,
					VersioningState.MINOR);
			// newDoc.addAcl(addAces, AclPropagation.OBJECTONLY);

			logger.debug("New document created: " + newDoc.getId());
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				throw new Exception(e);
			}
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				throw new Exception(e);
			}
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
	*Getter & Setter
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
	
	public void setUpload(File upload) {
	}
	
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
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