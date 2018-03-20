package jp.aegif.struts2cmisexplorer.struts2actions.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import jp.aegif.struts2cmisexplorer.domain.Aspect;
import jp.aegif.struts2cmisexplorer.domain.Property;
import jp.aegif.struts2cmisexplorer.util.PropertyManager;
import jp.aegif.struts2cmisexplorer.util.impl.PropertyManagerImpl;


import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tachibanakikaku.serverconnection.ServerConnection;


/**
 * Utilities in common use for Show/Update & Document/Folder Actions 
 * @author linzhixing
 */
public class AspectUtil {

	private static final String ASPECTS_BASE_URL = "aspects.base.url";
	private static final String NEMAKI_PROTOCOL = "nemaki.protocol";
	private static final String NEMAKI_HOST = "nemaki.host";
	private static final String NEMAKI_PORT = "nemaki.port";
	private static final String DISABLED = "disabled";
	
	
	//TODO エラー処理いれる
	/**
	 *Get aspects JSON provided by Neamki Server, and parse it to List. 
	 * @return
	 */
	public static List<Aspect> getNemakiAspects(){
		
		//Get response of Nemaki aspects list
		JSONObject response = new JSONObject();
		try {
			PropertyManager propertyManager = new PropertyManagerImpl();
			
			
			String protocol = propertyManager.readValue(NEMAKI_PROTOCOL);
			String host = propertyManager.readValue(NEMAKI_HOST);
			Integer port = Integer.parseInt(propertyManager.readValue(NEMAKI_PORT)); 
			
			ServerConnection conn = ServerConnection.createServerConnection(protocol,host, port);
			String path = propertyManager.readValue(ASPECTS_BASE_URL);
			response = conn.getJSON(path, new HashMap<String, Object>()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Get Aspects list
		List<Aspect> nemakiAspects = new ArrayList<Aspect>();
		if(response != null){
			//for each Nemaki aspect
			for(Object _name : response.keySet()){
				String aspectName = (String)_name;
				List<Property> properties = new ArrayList<Property>();
				JSONObject vals = (JSONObject) response.get(_name);
				
				//If aspect's "disabled" status exists and true, skip.
				Object _status = vals.get(DISABLED);
				if(_status != null){
					Boolean disabled = (Boolean) _status;
					if(disabled) continue;
				}

				//Get property value
				JSONArray _props = (JSONArray) vals.get("properties");
				for(Object _o : _props){
					JSONObject _prop = (JSONObject) _o;
					for(Object _key : _prop.keySet()){
						String propName = (String)_key;
						String propValue = (String) _prop.get(_key);
						Property property = new Property(propName, propValue);
						properties.add(property);
					}
				}
				Aspect aspect = new Aspect(aspectName, properties);
				nemakiAspects.add(aspect);
			}
		}
		return nemakiAspects;
	}	
	
	
	/**
	 * Build content's own aspects to show them.
	 * @param extElems
	 * @param nemakiAspects
	 * @return
	 */
	public static List<Aspect> getAspects(List<CmisExtensionElement> extElems, List<Aspect> nemakiAspects){
		List<Aspect> aspects = new ArrayList<Aspect>();

		//Retrieve aspects from CMIS Extension
		for(CmisExtensionElement extension : extElems){
			if("aspects".equals(extension.getName())){
				List<CmisExtensionElement> extAspects = extension.getChildren();	//Aspects extension

				if(!extAspects.isEmpty()){
					//Iterate to convert each aspect extension to Aspect object 
					for(CmisExtensionElement extAspect: extAspects){
						List<Property> properties = new ArrayList<Property>();
						Aspect nemakiAspect = getAspectByName(nemakiAspects, extAspect.getName());
						
						//Build property from aspect
						Set<String> docKeySet = extAspect.getAttributes().keySet();
						List<String> nemakiKeySet = makeKeySet(nemakiAspect.getProperties());
						for(String key : nemakiKeySet){		//nemakiKeySet doesn't include namespace items
							if(docKeySet.contains(key)){
								String val = extAspect.getAttributes().get(key);
								if(val == null) val = "";
								properties.add(new Property(key, val));			//Set property value
							}else{
								properties.add(new Property(key, ""));			//Set space value for the property, if the content has the aspect but not the property 
							}
						}
						
						//Build aspect setting its property
						Aspect aspect = new Aspect(extAspect.getName(), properties);
						aspects.add(aspect);
					}
				}
			}	
		}
		return aspects;
	}
	
	
	/**
	 * Build other aspects than content's aspects to show them
	 * @param aspects
	 * @param nemakiAspects
	 * @return
	 */
	public static List<Aspect> getOtherAspects(List<Aspect>aspects, List<Aspect> nemakiAspects){
		List<Aspect> otherAspects = new ArrayList<Aspect>();
		List<String>aspectsNameSet = makeNameSet(aspects);
		
		for(Aspect nemakiAspect : nemakiAspects){
			//Exclude the content's own aspects
			if(!aspectsNameSet.contains(nemakiAspect.getName())){
				Aspect otherAspect = new Aspect();
				otherAspect.setName(nemakiAspect.getName());
				List<Property> properties = makeSpaceValProperties(nemakiAspect.getProperties());		//Set property value SPACE.
				otherAspect.setProperties(properties);
				otherAspects.add(otherAspect);
			}
		}
		return otherAspects;
	}

	
	
	//TODO It might not be problem that namespace is not set in the update data.
	/**
	 * Build Cmis Extensions for aspect's update to Nemaki Server
	 * @param modifiedAspects
	 * @param docExts
	 * @param aspectDelChks
	 * @param aspectModChks
	 * @return
	 */
	
	public static List<CmisExtensionElement> buildExtensions(
			List<Aspect> modifiedAspects,
			List<CmisExtensionElement> docExts,
			List<String> aspectDelChks,
			List<String> aspectModChks) {

		List<CmisExtensionElement> extensions = new ArrayList<CmisExtensionElement>();
		List<CmisExtensionElement> aspectsList = new ArrayList<CmisExtensionElement>();

		//If delete chkbox is ON, delete the  whole aspect.
		//If modify chkbox is OFF, exclude the aspect from updating.
		for (int i = 0; i < modifiedAspects.size(); i++) {
			boolean delFlg = "true".equals(aspectDelChks.get(i));
			boolean modFlg = "true".equals(aspectModChks.get(i));

			// When delete chkbox & modify chkbox are both ON, skip.
			if (delFlg && modFlg) continue;

			//Retrieve the current aspect in the loop, which a user inputed.
			Aspect modifiedAspect = modifiedAspects.get(i);
			List<String> docAspectNames = makeNameSetFromExt(docExts);	//Get content's aspect names for checking the skip target
			
			//If modify chkbox is OFF & the aspect is not the content's own, skip.
			if (!modFlg && !docAspectNames.contains(modifiedAspect.getName())) continue;

			//Build property as CMIS extension for updating
			List<CmisExtensionElement> props = new ArrayList<CmisExtensionElement>();
			for (Property modifiedProperty : modifiedAspect.getProperties()) {
				// TODO ここvalue[0]で受け取ってるがいいのか
				String value = ((String[]) modifiedProperty.getValue())[0];

				if (!value.equals("")) {
					CmisExtensionElementImpl prop = new CmisExtensionElementImpl(
							"", modifiedProperty.getKey(), null, value);
					props.add(prop);
				}
			}
			
			//Build aspect as CMIS extension for updating
			CmisExtensionElement aspect = new CmisExtensionElementImpl("",
					modifiedAspect.getName(), null, props);
			aspectsList.add(aspect);
		}
		
		//Build aspects as CMIS extension for updating (content's CMIS extension includes "aspects" and "pastVersions" on the root level)
		CmisExtensionElement aspects = new CmisExtensionElementImpl("",
				"aspects", null, aspectsList);
		extensions.add(aspects);
		return extensions;
	}
	
	
	/**
	 * List operation utility
	 * 
	 */
	
	public static Aspect getAspectByName(List<Aspect>aspects, String name){
		for(Aspect aspect : aspects){
			if(name.equals(aspect.getName())){
				return aspect;
			}
		}
		return null;
	}

	public static Property getPropertyByKey(List<Property>properties, String key){
		for(Property property : properties){
			if(key.equals(property.getKey())){
				return property;
			}
		}
		return null;
	}
	
	public static List<String> makeKeySet(List<Property> properties){
		List<String> keySet = new ArrayList<String>();
		
		for(Property property : properties){
			keySet.add(property.getKey()); 
		}
		return keySet;
	}
	
	public static List<String> makeNameSet(List<Aspect> aspects){
		List<String> nameSet = new ArrayList<String>();
		for(Aspect aspect : aspects){
			nameSet.add(aspect.getName());
		}
		return nameSet;
	}
	
	private static List<Property> makeSpaceValProperties(List<Property> properties){
		List<Property> spaceProperties = new ArrayList<Property>();
		for(Property property : properties){
			spaceProperties.add(new Property(property.getKey(), ""));
		}
		return spaceProperties;
	}
	
	private static List<String> makeNameSetFromExt(List<CmisExtensionElement> exts){
		List<String> list = new ArrayList<String>();
		for(CmisExtensionElement ext : exts){
			if(ext.getName().equals("aspects")){
				for(CmisExtensionElement aspect : ext.getChildren()){
					list.add(aspect.getName());
				}
			}
		}
		return list;
	}
	
	public static List<Aspect> sortAspects(List<Aspect>aspects){
		List<Aspect> list = new ArrayList<Aspect>();
		List<String> nameSet = makeNameSet(aspects);
		Collections.sort(nameSet);
		for (String name : nameSet){
			list.add(getAspectByName(aspects, name));
		}
		return list;
	}
}
