package jp.aegif.struts2cmisexplorer.opencmisbinding.util;

import com.opensymphony.xwork2.ActionSupport;

public class FacadeUtil extends ActionSupport{
	
	public int maxItemsPerPage;

	public static int getMaxItemsPerPage() {
		ActionSupport actionSupport = new ActionSupport();
		return Integer.parseInt(actionSupport.getText("maxItemsPerPage"));
	}
}