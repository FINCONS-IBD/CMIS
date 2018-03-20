package jp.aegif.struts2cmisexplorer.domain.comparator;

import java.util.Comparator;
import java.util.GregorianCalendar;

import jp.aegif.struts2cmisexplorer.domain.Node;


public class VersionComparator implements Comparator {
	@Override
	public int compare(Object o1, Object o2) {
		Node n1 = (Node)o1;
		Node n2 = (Node)o2;
		
		GregorianCalendar gc1 = n1.getCreationDate();
		GregorianCalendar gc2 = n2.getCreationDate();
		if(gc1.after(gc2)){
			return -1;
		}else if(gc1.before(gc2)){
			return 1;
		}else{
			return 0;
		}
	}
}
