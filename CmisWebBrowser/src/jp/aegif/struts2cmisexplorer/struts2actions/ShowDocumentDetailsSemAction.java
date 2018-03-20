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
import java.util.Map;


import jp.aegif.struts2cmisexplorer.domain.Aspect;
import jp.aegif.struts2cmisexplorer.domain.RepositoryClientFacade;
import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.struts2actions.base.DocumentActionBase;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fincons.util.PropertiesHelper;
import com.fincons.util.RDFQuadruple;
import com.fincons.util.VirtuosoClientApi;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.opensymphony.xwork2.Action;

public class ShowDocumentDetailsSemAction extends DocumentActionBase {

	private static final long serialVersionUID = -1467744015769465948L;
	private static final Log logger = LogFactory
			.getLog(ShowDocumentDetailsSemAction.class);

	//	private List<Node> versionInfo;

	private List<Aspect> otherAspects;

	public List<Aspect> getOtherAspects() {
		return otherAspects;
	}

	public void setOtherAspects(List<Aspect> otherAspects) {
		this.otherAspects = otherAspects;
	}
	@SuppressWarnings("unchecked")
	@Override
	public String execute() {

		try {

			RepositoryClientFacade facade = getFacade();
			if(facade == null) return ERROR;


			CmisObject content = facade.getSession().getObject(
					getModel().getId());

			String id = content.getId().substring(0, content.getId().indexOf(";"));

			String graphUrl = PropertiesHelper.getProps().getProperty("processedFileLocation")+id+".html";
			
			ResultSet results = VirtuosoClientApi.retrieveDocDetails(graphUrl);
			
			List<RDFQuadruple> semanticTriples = new ArrayList<RDFQuadruple>();
						
			int count = 0;
			while (results.hasNext()) {
				QuerySolution result = results.nextSolution();
				RDFNode a = result.get("subject");  
				RDFNode b = result.get("predicate");
				RDFNode c = result.get("object");
				RDFNode d = result.get("label");				
				
				semanticTriples.add(new RDFQuadruple(a, b, c, d));
								
				count++;
			}

			getModel().setSemanticTriples(semanticTriples);

			String detailsDocJson = VirtuosoClientApi.getDocJSONFromVirtuoso(graphUrl);
					
			getModel().setDetailsDocJson(detailsDocJson);
			
//			System.out.println("Found " + count + " results");

		} catch (ConnectionFailedException e) {
			logger.error(e);
			addActionError(getText("connection_failed"));
			return INPUT;
		}
		/*

		Map<String, Object> valueStack = new HashMap<String, Object>();

		try {
			RepositoryClientFacade facade = getFacade();
			if(facade == null) return ERROR;
			CmisObject content = facade.getSession().getObject(
					getModel().getId());
			if (content instanceof DocumentImpl) {
				DocumentImpl d = (DocumentImpl) content;
				getModel().setAcl(d.getAcl().getAces());
				getModel().setName(d.getName());
				getModel().setType("cmis:document");
				getModel().setSize(d.getContentStreamLength());
				getModel().setCreatedBy(d.getCreatedBy());
				getModel().setCreationDate(d.getCreationDate());
				getModel().setLastModifiedBy(d.getLastModifiedBy());
				getModel().setLastModificationDate(d.getLastModificationDate());
				getModel().setPath(StringUtils.join(d.getPaths(), '/'));
//				List<String> paths = d.getPaths();//test

				List<org.apache.chemistry.opencmis.client.api.Folder> parents = d.getParents();
				String parent = parents.get(0).getId();
				getModel().setParent(parent);


				Document doc = (Document) facade.getSession().getObject(getModel().getId());
				ContentStream contentStream = doc.getContentStream(); // returns null if the document has no content

				if (contentStream != null) {

				    Map<String, String> metadataMap = TikaApi.parseToStringExample(contentStream.getStream());
					String documentContent = metadataMap.get("documentContent");
				    metadataMap.remove("documentContent"); //remove the document content from the metadata
				    getModel().setTikaMetadata(metadataMap); 

				    String noSurrogateText = CleanSurrogateChars.cleanTextBySurrogate(documentContent);

					String textRazorApiKey = PropertiesHelper.getProps().getProperty("textRazorApiKey");
				    AnalysisResult result = TextRazorApi.testAnalysis(textRazorApiKey, d.getVersionSeriesId(), noSurrogateText);

					//creating the html file with the highlined entity
				    HTMLTagEditor.htmlDocumentTagging(d.getVersionSeriesId(), noSurrogateText, result);

				    String graphUrl = VirtuosoSpongerClient.startSpongerTagging(d.getVersionSeriesId());
				    logger.info("Sponger Process completed. Create the graph " + graphUrl);

				    //enrich the sponger graph with textrazor entity retrieved
					TextRazorVirtuosoHelper.convertAnalysisInTriples(graphUrl, result.getEntityList());

				    DocumentCustom cmisDoc = new DocumentCustom();
				    cmisDoc.setMetadataMap(metadataMap);
				    cmisDoc.setContentAnalysis(result.getEntityList());
				    cmisDoc.setCmisDocumentId(d.getId());
				    cmisDoc.setCmisDownloadUrl(d.getContentUrl());

				    getModel().setTextRazorAnalysis(result.getEntityList());

				    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				    String json = gson.toJson(cmisDoc);
				    json = json.replace("'", "\\'");
				    System.out.println(json);
				    getModel().setJsonAnalysis(json);
				} else {
					 logger.debug("No content for " + d.getName());
				}

//				List<CmisExtensionElement> extElems = content.getExtensions(
//						ExtensionLevel.OBJECT);


//				if(extElems != null) {
//				   // this object has no extensions on this level
//				   return;
//				}

				//set pastVersions
//				Map<String,String> pastVersions = new HashMap<String, String>();
//				for(CmisExtensionElement elem : extElems){
//					if ("pastVersions".equals(elem.getName())){
//						pastVersions = elem.getAttributes();
//					}
//				}

//				Collection<String> dispPastVersions = new ArrayList<String>();
//				for(String key : pastVersions.keySet()){
//						//TODO change to Regular Expression
//						if(key.substring(0, 1).equals("v")){
//							dispPastVersions.add(pastVersions.get(key));
//						}
//				}
//				Collections.reverse((List<String>) dispPastVersions); //reverse to descending the order
//				getModel().setVersions(dispPastVersions.toArray(new String[0]));

				//Get versionInfo
//				String[] vs = getModel().getVersions();
//				this.versionInfo = new ArrayList<Node>();
//				for(String versionId : getModel().getVersions()){
//					DocumentImpl versionObj = (DocumentImpl) facade.getSession().getObject(new ObjectIdImpl(versionId));
//					Node versionNode = new Node();
//					versionNode.setId(versionObj.getId());
//					versionNode.setName(versionObj.getName());
//					versionNode.setCreatedBy(versionObj.getCreatedBy());
//					versionNode.setCreationDate(versionObj.getCreationDate());
//					versionNode.setSize(versionObj.getContentStreamLength());
//					this.versionInfo.add(versionNode);
//				}
//				//sort by creationDate
//				Collections.sort(versionInfo, new VersionComparator());
//				
//				//push versionInfo to ValueStack
//				valueStack.put("versionInfo", versionInfo);
//				
//				//set aspects
//				List<Aspect> nemakiAspects = AspectUtil.getNemakiAspects();
//				List<Aspect> docAspects = AspectUtil.getAspects(extElems, nemakiAspects);
//				List<Aspect> otherAspects = AspectUtil.getOtherAspects(docAspects, nemakiAspects);
//				//sort
//				docAspects = AspectUtil.sortAspects(docAspects);
//				otherAspects = AspectUtil.sortAspects(otherAspects);
//				//register content's own aspects and Nemaki aspects
//				getModel().setAspects(docAspects);
//				valueStack.put("otherAspects", otherAspects);

//				try {
					//register parent folder
					String folder = getModel().getParent();
					ActionContext.getContext().getSession().put("folder", folder);

					//Get NodeListPage
//					NodesListPage page = getFacade().getNodesListPage(folder,0);

//				} catch (ConnectionFailedException e) {
//					e.printStackTrace();
//				}

				//register to ValueStack
				ActionContext.getContext().getValueStack().push(valueStack);
			} else {
				return ERROR;
			}
			if (logger.isDebugEnabled())
				logger.debug("model: " + getModel());
		} catch (ConnectionFailedException e) {
			logger.error(e);
			addActionError(getText("connection_failed"));
			return INPUT;
		} catch (SAXException e) {
			logger.error(e);
			addActionError(getText("nlp_error"));
			return INPUT;
		} catch (TikaException e) {
			logger.error(e);
			addActionError(getText("nlp_error"));
			return INPUT;
		} catch (IOException e) {
			logger.error(e);
			addActionError(getText("nlp_error"));
			return INPUT;
		} catch (AnalysisException e) {
			logger.error(e);
			addActionError(getText("nlp_error"));
			return INPUT;
		}

		 */

		return Action.SUCCESS;
	}

	//Breadcrumb(Folder tree, in fact) for the current folder
	public List<Map<String,String>> setBreadcrumb(){
		//return ActionUtil.setBreadcrumb(getFacade(), getModel().getParent());
		return buildParentsList(getModel().getParent());
	}

}
