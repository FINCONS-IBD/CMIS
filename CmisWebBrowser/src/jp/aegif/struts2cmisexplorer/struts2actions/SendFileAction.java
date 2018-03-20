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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.aegif.struts2cmisexplorer.domain.Document;
import jp.aegif.struts2cmisexplorer.domain.exceptions.ConnectionFailedException;
import jp.aegif.struts2cmisexplorer.domain.exceptions.NotLoggedInException;
import jp.aegif.struts2cmisexplorer.domain.exceptions.UnauthorizedException;

/**
 * Struts2 Action support bean to send a file.
 */
public class SendFileAction extends AuthenticatedAction {

	private static final long serialVersionUID = -9124829157991446153L;

	/**
	 * Identifier of the file to be sent.
	 */
	private String node;

	/**
	 * Document to be sent.
	 */
	private Document document;

	/**
	 * Struts2 execution.
	 */
	@Override
	public String execute() {
		try {
			getCredentials();
		} catch (NotLoggedInException e) {
			return LOGIN;
		}
		try {
			document = getFacade().getDocument(node);
		} catch (UnauthorizedException e) {
			return "unauthorized";
		} catch (ConnectionFailedException e) {
			return LOGIN;
		}
		return SUCCESS;
	}

	/**
	 * Getters / Setters
	 */
	public void setNode(String node) {
		this.node = node;
	}

	public InputStream getInputStream() {
		return document.getInputStream();
	}

	public String getContentType() {
		return document.getContentType();
	}

	public long getContentLength() {
		return document.getContentLength();
	}

	public String getFilename() {
		return document.getFilename();
	}

	public String getUrlEncodedFilename() throws UnsupportedEncodingException {
		return URLEncoder.encode(document.getFilename(), "UTF-8");
	}
}