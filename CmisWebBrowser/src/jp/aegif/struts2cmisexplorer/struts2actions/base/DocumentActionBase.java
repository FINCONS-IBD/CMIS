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
package jp.aegif.struts2cmisexplorer.struts2actions.base;

import jp.aegif.struts2cmisexplorer.domain.Document;
import jp.aegif.struts2cmisexplorer.struts2actions.AuthenticatedAction;

import com.opensymphony.xwork2.ModelDriven;

public class DocumentActionBase extends AuthenticatedAction implements
		ModelDriven<Document> {

	private static final long serialVersionUID = 8062785443762056489L;

	private Document documentModel = new Document();

	@Override
	public Document getModel() {
		return documentModel;
	}

	/**
	 * Set parent folder id for action chain.<br/>
	 * This value is used by ShowFolderAction
	 */
	public String getFolder() {
		return documentModel.getParent();
	}

}