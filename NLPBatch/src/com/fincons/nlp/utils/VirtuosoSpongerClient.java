package com.fincons.nlp.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import virtuoso.jena.driver.VirtGraph;

public class VirtuosoSpongerClient {

	private static final Log logger = LogFactory
			.getLog(VirtuosoSpongerClient.class);

	public static int startSpongerTagging(String graphUrlToProcess) throws ResourceException {


		String endpointSponger = PropertiesHelper.getProps().getProperty("endpointSpongerUrl");

		String dbUser = PropertiesHelper.getProps().getProperty("virtuosoDbUser");
		String dbPassword = PropertiesHelper.getProps().getProperty("virtuosoDbPsw");
		String virtuosoEndPoint = PropertiesHelper.getProps().getProperty("virtuosoEndpoint");



		//clear existing graph			
		VirtGraph graph = new VirtGraph (graphUrlToProcess, virtuosoEndPoint, dbUser, dbPassword);
		graph.clear();

		int status = 500;

		Client client = new Client(new Context(), Protocol.HTTP);

		String urlToCall = endpointSponger+graphUrlToProcess;
		ClientResource clientResource = new ClientResource(urlToCall);
		
		logger.debug("Url to call: " + urlToCall);
		
		clientResource.setNext(client);

		Representation serverResponse = null;


		serverResponse = clientResource.get();

		logger.debug("Sponger server response: \n" + serverResponse);
		status = clientResource.getStatus().getCode();
		
		return status;

	}

}
