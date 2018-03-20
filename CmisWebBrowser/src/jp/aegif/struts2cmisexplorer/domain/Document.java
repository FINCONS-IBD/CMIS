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
package jp.aegif.struts2cmisexplorer.domain;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fincons.util.EntityCustom;
import com.fincons.util.RDFQuadruple;
import com.fincons.util.RDFTriple;
import com.hp.hpl.jena.graph.Triple;

/**
 * In Alfresco and CMIS vocabulary, a "document" in a file in the repository. It
 * can not be a folder.
 */
public class Document extends Node {

	/**
	 * Input stream from the file.
	 */
	private InputStream inputStream;

	/**
	 * Content type of the file (MIME, for instance text/html)
	 */
	private String contentType;

	/**
	 * Size of the file. It is used by web browsers to show a progression bar.
	 */
	private long contentLength;

	/**
	 * Name of the file.
	 */
	private String filename;

	private File upload;
	private String[] versions;
	private List<Aspect> aspects;
	
	//the set of triples containing the output of semantic processing
	private List<RDFQuadruple> semanticTriples;
	
	//contains the statistic json used by PieChart
	private String detailsDocJson;

	public List<RDFQuadruple> getSemanticTriples() {
		return semanticTriples;
	}

	public void setSemanticTriples(List<RDFQuadruple> semanticTriples) {
		this.semanticTriples = semanticTriples;
	}

	//added the tika metadata collection
	Map<String, String> tikaMetadata;
	
	public void setTikaMetadata(Map<String, String> tikaMetadata) {
		this.tikaMetadata = tikaMetadata;
	}

	public Map<String, String> getTikaMetadata() {
		return tikaMetadata;
	}
	
	private Set<EntityCustom> textRazorAnalysis;

	public Set<EntityCustom> getTextRazorAnalysis() {
		return textRazorAnalysis;
	}
			    
	public void setTextRazorAnalysis(Set<EntityCustom> textRasorAnalysis) {
		this.textRazorAnalysis = textRasorAnalysis;
	}
	
	private String jsonAnalysis;
	
	public String getJsonAnalysis() {
		return jsonAnalysis;
	}

	public void setJsonAnalysis(String jsonAnalysis) {
		this.jsonAnalysis = jsonAnalysis;
	}	

	public String getDetailsDocJson() {
		return detailsDocJson;
	}

	public void setDetailsDocJson(String detailsDocJson) {
		this.detailsDocJson = detailsDocJson;
	}

	/**
	 * Constructor.
	 */
	public Document(InputStream inputStream, String contentType,
			long contentLength, String filename) {
		this.inputStream = inputStream;
		this.contentType = contentType;
		this.contentLength = contentLength;
		this.filename = filename;
	}

	public Document() {
	}

	/**
	 * Getters.
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	public String getContentType() {
		return contentType;
	}

	public long getContentLength() {
		return contentLength;
	}

	public String getFilename() {
		return filename;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String[] getVersions() {
		return versions;
	}

	public void setVersions(String[] versions) {
		this.versions = versions;
	}

	public List<Aspect> getAspects() {
		return aspects;
	}

	public void setAspects(List<Aspect> aspects) {
		this.aspects = aspects;
	}


	
	
	
}