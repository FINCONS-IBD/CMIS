package com.fincons.nlp.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.CmisBindingFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;


public class CMISClient {

	private static final String alfrescoProtocol = PropertiesHelper.getProps().getProperty("nemaki.protocol");
	private static final String alfrescoIp = PropertiesHelper.getProps().getProperty("nemaki.host");
	private static final String alfrescoPort = PropertiesHelper.getProps().getProperty("nemaki.port");
	private static final String alfrescoUser = PropertiesHelper.getProps().getProperty("nemaki.user");
	private static final String alfrescoPsw = PropertiesHelper.getProps().getProperty("nemaki.psw");
	private static final String atompubUrl = PropertiesHelper.getProps().getProperty("cmis.atompub.url");


	public static Session getSession() {

		Session session = null;

		// Default factory implementation of client runtime.
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();

		// User credentials.
		parameter.put(SessionParameter.USER, alfrescoUser);		//This should be user id, not user name. 
		parameter.put(SessionParameter.PASSWORD, alfrescoPsw);

		// Connection settings.

		try {
			URL url = new URL(alfrescoProtocol, 
					alfrescoIp, 
					Integer.parseInt(alfrescoPort), 
					atompubUrl);

			String urlString = url.toString();
			parameter.put(SessionParameter.ATOMPUB_URL, urlString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		parameter.put(SessionParameter.BINDING_TYPE,
				BindingType.ATOMPUB.value());

		// Session locale.
		parameter.put(SessionParameter.LOCALE_ISO3166_COUNTRY, "");
		parameter.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "en");
		parameter.put(SessionParameter.LOCALE_VARIANT, "US");

		// Authentication
		parameter.put(SessionParameter.AUTH_HTTP_BASIC, "true");
		parameter.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS,
				CmisBindingFactory.STANDARD_AUTHENTICATION_PROVIDER);

		parameter.put(SessionParameter.REPOSITORY_ID, "48411d37-148c-492f-988d-633932f8d91c");
		
				
		Repository soleRepository = sessionFactory.getRepositories(
				parameter).get(0);
		session = soleRepository.createSession();

		return session;
	}

}