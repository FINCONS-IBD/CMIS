package com.fincons.nlp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fincons.nlp.utils.AnalysisResult;
import com.fincons.nlp.utils.CMISClient;
import com.fincons.nlp.utils.CleanSurrogateChars;
import com.fincons.nlp.utils.HTMLTagEditor;
import com.fincons.nlp.utils.PropertiesHelper;
import com.fincons.nlp.utils.TextRazorApi;
import com.fincons.nlp.utils.TextRazorVirtuosoHelper;
import com.fincons.nlp.utils.TikaApi;
import com.fincons.nlp.utils.VirtuosoSpongerClient;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.DocumentImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.server.support.query.CalendarHelper;
import org.apache.tika.exception.TikaException;
import org.restlet.resource.ResourceException;
import org.xml.sax.SAXException;

import com.textrazor.AnalysisException;

public class NLPBatchRunnableLight {

	private static final long serialVersionUID = -1467744015769465948L;
	private static final Log logger = LogFactory
			.getLog(NLPBatchRunnableLight.class);

	public static void main(String[] args) {

		String testFolderId = PropertiesHelper.getProps().getProperty("cmis.testFolderId");
		
		logger.info("Batch Started succesfull on folder " + testFolderId);
		
	}

}
