package jp.aegif.struts2cmisexplorer.struts2actions.util;

import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ApiUtil {

	// TODO ここと関係ないけど、FacadeUtilの移動
	@SuppressWarnings("unchecked")
	public static JSONArray sortList(JSONArray list, String jtSorting) {
		String[] _jtSorting = StringUtils.split(jtSorting);

		final String field = _jtSorting[0];
		final String direction = _jtSorting[1];

		Collections.sort(list, new Comparator<Object>() {
			public boolean equals(Object obj) {
				return super.equals(obj);
			}

			public int compare(Object o1, Object o2) {
				JSONObject _o1 = (JSONObject) o1;
				JSONObject _o2 = (JSONObject) o2;

				Class clazz = (_o1.get(field)).getClass();

				if (clazz.equals(java.lang.Long.class)) {

					Long long1 = (Long) _o1.get(field);
					Long long2 = (Long) _o2.get(field);

					if (direction.equals("ASC")) {
						return long1.compareTo(long2);
					} else if (direction.equals("DESC")) {
						return long2.compareTo(long1);
					}
				} else if (clazz.equals(java.lang.String.class)) {
					String o1Key = (String) _o1.get(field);
					String o2Key = (String) _o2.get(field);

					if (direction.equals("ASC")) {
						return o1Key.compareTo(o2Key);
					} else if (direction.equals("DESC")) {
						return o2Key.compareTo(o1Key);
					}
				} else {
					return 0;
				}
				return 0;
			}
		});

		return list;
	}

}
