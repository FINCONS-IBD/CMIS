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
package jp.aegif.struts2cmisexplorer.opencmisbinding;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.aegif.struts2cmisexplorer.domain.Aspect;
import jp.aegif.struts2cmisexplorer.domain.Document;
import jp.aegif.struts2cmisexplorer.domain.Node;
import jp.aegif.struts2cmisexplorer.domain.NodesListPage;
import jp.aegif.struts2cmisexplorer.domain.Property;
import jp.aegif.struts2cmisexplorer.domain.RepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.domain.exceptions.UnauthorizedException;
import jp.aegif.struts2cmisexplorer.util.PropertyManager;
import jp.aegif.struts2cmisexplorer.util.impl.PropertyManagerImpl;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.CmisBindingFactory;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of a repository facade, using OpenCMIS.
 */
public class OpenCMISRepositoryClientFacade implements RepositoryClientFacade {

	// ///////////////////////////////////////////////////////
	// /////////////// CONFIGURATION : BEGIN /////////////////
	// ///////////////////////////////////////////////////////

	/**
	 * URL of the CMIS repository.
	 */
	protected final static String CMIS_ATOMPUB_URL = "cmis.atompub.url";		//property key
	private static final String NEMAKI_PROTOCOL = "nemaki.protocol";
	private static final String NEMAKI_HOST = "nemaki.host";
	private static final String NEMAKI_PORT = "nemaki.port";
	
	private static final Log log = LogFactory
			.getLog(OpenCMISRepositoryClientFacade.class);
	

	/**
	 * Number of results displayed by page (pagination). Any positive value is
	 * fine.
	 */
	//
	//private final static int MAX_ITEMS_PER_PAGE = 50;
	
	// ///////////////////////////////////////////////////////
	// //////////////// CONFIGURATION : END //////////////////
	// ///////////////////////////////////////////////////////

	/**
	 * Username for the CMIS session.
	 */
	private String user;

	/**
	 * Password for the CMIS session.
	 */
	private String password;

	/**
	 * Cache for the session object, so that only one session is created by user
	 * request.
	 */
	private Session session;

	/**
	 * CMIS operation context, it contains a pagination setting.
	 */
	private OperationContext operationContext;

	/**
	 * Constructor.
	 */
	public OpenCMISRepositoryClientFacade(String user, String password) {
		this.user = user;
		this.password = password;
		operationContext = new OperationContextImpl();
		operationContext.setMaxItemsPerPage(getMaxItemsPerPage());
		operationContext.setIncludeAcls(true);
		operationContext.setIncludeAllowableActions(true);
		operationContext.setIncludePathSegments(true);
	}

	/**
	 * Get a CMIS session.
	 */
	public Session getSession() throws ConnectionFailedException {
		log.debug("#getSession. Current Session is " + session);
		if (session != null)
			return session;

		// Default factory implementation of client runtime.
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();

		// User credentials.
		parameter.put(SessionParameter.USER, user);		//This should be user id, not user name. 
		parameter.put(SessionParameter.PASSWORD, password);

		// Connection settings.
		PropertyManager propertyManager = new PropertyManagerImpl();
		try {
			URL url = new URL(propertyManager.readValue(NEMAKI_PROTOCOL), 
							  propertyManager.readValue(NEMAKI_HOST), 
							  Integer.parseInt(propertyManager.readValue(NEMAKI_PORT)), 
							  propertyManager.readValue(CMIS_ATOMPUB_URL));
		
			String urlString = url.toString();
			parameter.put(SessionParameter.ATOMPUB_URL, urlString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		parameter.put(SessionParameter.BINDING_TYPE,
				BindingType.ATOMPUB.value());

		// Session locale.
		parameter.put(SessionParameter.LOCALE_ISO3166_COUNTRY, "");
		parameter.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "en");
		parameter.put(SessionParameter.LOCALE_VARIANT, "US");

		// Authentication
		parameter.put(SessionParameter.AUTH_HTTP_BASIC, "true");
		parameter.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS,
				CmisBindingFactory.STANDARD_AUTHENTICATION_PROVIDER);

		// Create session.
		// To avoid extra configuration: only one repository per CMIS server.
//		Session session = null;
		try {
			Repository soleRepository = sessionFactory.getRepositories(
					parameter).get(0);
			session = soleRepository.createSession();
			log.debug("New session created.");
		} catch (CmisConnectionException e) {
			throw new ConnectionFailedException(e);
		} catch (CmisRuntimeException e) {
			throw new UnauthorizedException(e);
		}
		session.setDefaultContext(operationContext);
		return session;
	}

	/**
	 * List the content of a folder. More specifically, get a page of items from
	 * the given folder, starting at the given offset.
	 */
	@Override
	public NodesListPage getNodesListPage(String folderId, int skipCount)
			throws ConnectionFailedException {
		CmisObject object = getSession().getObject(new ObjectIdImpl(folderId));
		Folder folder = (Folder) object;
		ItemIterable<CmisObject> children = folder.getChildren(operationContext);
		//children.skipTo(skipCount);
		ItemIterable<CmisObject> page = children.skipTo(skipCount).getPage();
		Iterator<CmisObject> iterator = page.iterator();
		long totalNumberOfNodes = children.getTotalNumItems();
		List<Node> list = new ArrayList<Node>((int)totalNumberOfNodes);
		while (iterator.hasNext()) {
			CmisObject child = iterator.next();
			Node n = setNodeInfo(child);
			list.add(n);
		}
		return new NodesListPage(list, totalNumberOfNodes);
	}

	/**
	 * Get the identifier (Node Reference) of a document, given its path.
	 * Example of path: /Data Dictionary/Messages
	 */
	@Override
	public String getNodeRef(String path) throws ConnectionFailedException {
		CmisObject object = getSession().getObjectByPath(path);
		return object.getId();
	}

	/**
	 * Get a document.
	 */
	@Override
	public Document getDocument(String fileId) throws ConnectionFailedException {
		CmisObject object = getSession().getObject(new ObjectIdImpl(fileId));
		org.apache.chemistry.opencmis.client.api.Document openCMISDocument = (org.apache.chemistry.opencmis.client.api.Document) object;
		Document document = new Document(openCMISDocument.getContentStream()
				.getStream(), openCMISDocument.getContentStreamMimeType(),
				openCMISDocument.getContentStreamLength(),
				openCMISDocument.getName());
		return document;
	}

	/**
	 * Get a folder.
	 */
	@Override
	public Folder getFolder(String folderId) throws ConnectionFailedException {
		Folder folder = (Folder) getSession().getObject(
				new ObjectIdImpl(folderId));
		return folder;
	}

	/**
	 * Number of results displayed by page (pagination).
	 * The number is injected from global.properties
	 */
	@Override
	public int getMaxItemsPerPage() {
		int max = jp.aegif.struts2cmisexplorer.opencmisbinding.util.FacadeUtil.getMaxItemsPerPage();
		return max;
		//return MAX_ITEMS_PER_PAGE;
	}
	
	public Node getNode(String id) throws ConnectionFailedException{
			
		CmisObject obj = getSession().getObject(new ObjectIdImpl(id), operationContext);
		Node n = setNodeInfo(obj);
		return n;
	}
	
	public Node setNodeInfo(CmisObject obj){
		Node n = new Node(obj.getId(), obj.getName(), isFolder(obj));
		n.setCreationDate(obj.getCreationDate());
		n.setLastModificationDate(obj.getLastModificationDate());
		n.setCreatedBy(obj.getCreatedBy());
		n.setLastModifiedBy(obj.getLastModifiedBy());
		if (obj.getAllowableActions().getAllowableActions()
				.contains(Action.CAN_APPLY_ACL))
			n.setOwner(true);
		if (obj.getAllowableActions().getAllowableActions()
				.contains(Action.CAN_UPDATE_PROPERTIES))
			n.setCollaborator(true);
		if (obj.getAllowableActions().getAllowableActions()
				.contains(Action.CAN_GET_PROPERTIES))
			n.setConsumer(true);

		if (!isFolder(obj)) {
			n.setMimetype(((org.apache.chemistry.opencmis.client.api.Document) obj)
					.getContentStreamMimeType());
			n.setSize(((org.apache.chemistry.opencmis.client.api.Document) obj)
					.getContentStreamLength());
		}
		
		//Set aspects info by iterating CMIS extension
		List<CmisExtensionElement> extensions = obj.getExtensions(ExtensionLevel.OBJECT);
		if(extensions != null){
			
			for(CmisExtensionElement extension : extensions){
				
				if("aspects".equals(extension.getName())){
					List<CmisExtensionElement> extAspects = extension.getChildren();
					List<Aspect> aspects = new ArrayList<Aspect>();
					if(!extAspects.isEmpty()){
						for(CmisExtensionElement extAspect: extAspects){
							//build property
							List<Property> properties = new ArrayList();
							for(String key : extAspect.getAttributes().keySet()){
								Property property = new Property(key, extAspect.getAttributes().get(key));
								properties.add(property);
							}
							Aspect aspect = new Aspect(extAspect.getName(), properties);
							aspects.add(aspect);
						}
						n.setAspects(aspects);	//Set aspects
					}
				}
			}
		}
		
		return n;
	}
	
	/**
	 * Whether the given object is a folder or not.
	 */
	private static boolean isFolder(CmisObject object) {
		return object.getBaseTypeId().value()
//				.equals(ObjectType.FOLDER_BASETYPE_ID);
				.equals(BaseTypeId.CMIS_FOLDER.value());
	}
}
