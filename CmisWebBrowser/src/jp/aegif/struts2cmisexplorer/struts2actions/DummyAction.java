package jp.aegif.struts2cmisexplorer.struts2actions;

/**
 * Dummy action which always return sucess
 * 
 * @author mryoshio
 * 
 */
public class DummyAction extends AuthenticatedAction {

	private static final long serialVersionUID = 6621205354654725317L;

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
}
