package jp.aegif.struts2cmisexplorer.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlPrincipalDataImpl;

public class ACLService {

	public static Acl mergeAcls(Acl originalAces, Acl addAces, Acl removeAces) {
		
		Map<String, Set<String>> originals = convertAclToMap(originalAces);
		Map<String, Set<String>> adds = convertAclToMap(addAces);
		Map<String, Set<String>> removes = convertAclToMap(removeAces);
		List<Ace> newAces = new ArrayList<Ace>();
		// iterate through the original ACEs
		for (Map.Entry<String, Set<String>> ace : originals.entrySet()) {

			// add permissions
			Set<String> addPermissions = adds.get(ace.getKey());
			if (addPermissions != null) {
				ace.getValue().addAll(addPermissions);
			}
			// remove permissions
			Set<String> removePermissions = removes.get(ace.getKey());
			if (removePermissions != null) {
				ace.getValue().removeAll(removePermissions);
			}
			// create new ACE
			if (!ace.getValue().isEmpty()) {
				newAces.add(new AccessControlEntryImpl(
						new AccessControlPrincipalDataImpl(ace.getKey()),
						new ArrayList<String>(ace.getValue())));
			}
		}

		// find all ACEs that should be added but are not in the original ACE
		// list
		for (Map.Entry<String, Set<String>> ace : adds.entrySet()) {
			if (!originals.containsKey(ace.getKey())
					&& !ace.getValue().isEmpty()) {
				newAces.add(new AccessControlEntryImpl(
						new AccessControlPrincipalDataImpl(ace.getKey()),
						new ArrayList<String>(ace.getValue())));
			}
		}

		return new AccessControlListImpl(newAces);
	}

	private static Map<String, Set<String>> convertAclToMap(Acl acl) {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();

		if (acl == null || acl.getAces() == null) {
			return result;
		}

		for (Ace ace : acl.getAces()) {
			// don't consider indirect ACEs - we can't change them
			if (!ace.isDirect()) {
				// ignore
				continue;
			}

			// although a principal must not be null, check it
			if (ace.getPrincipal() == null
					|| ace.getPrincipal().getId() == null) {
				// ignore
				continue;
			}

			Set<String> permissions = result.get(ace.getPrincipal().getId());
			if (permissions == null) {
				permissions = new HashSet<String>();
				result.put(ace.getPrincipal().getId(), permissions);
			}

			if (ace.getPermissions() != null) {
				permissions.addAll(ace.getPermissions());
			}
		}

		return result;
	}

}
