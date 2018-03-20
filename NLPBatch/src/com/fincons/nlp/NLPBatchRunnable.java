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

public class NLPBatchRunnable {

	private static final long serialVersionUID = -1467744015769465948L;
	private static final Log logger = LogFactory
			.getLog(NLPBatchRunnable.class);

	public static void main(String[] args) {

		String testFolderId = PropertiesHelper.getProps().getProperty("cmis.testFolderId");
		
		logger.info("Batch Started succesfull");

		Session session = CMISClient.getSession();

		Calendar cal;

		Date date = new Date();

		cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 00); 
		//		cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 00);
		//        cal.set(Calendar.MINUTE, 50); 
		cal.set(Calendar.SECOND, 00); 
		cal.set(Calendar.MILLISECOND, 001);
		String convertedMin = CalendarHelper.toString(cal);

		cal.set(Calendar.HOUR_OF_DAY, 23); 
		cal.set(Calendar.MINUTE, 59); 
		cal.set(Calendar.SECOND, 59); 
		cal.set(Calendar.MILLISECOND, 999); 

		String convertedMax = CalendarHelper.toString(cal);
		
		String query = "SELECT * FROM cmis:document where cmis:objectTypeId='cmis:document' "
				+ "AND cmis:creationDate>= TIMESTAMP '"+convertedMin+"' "
				+ "AND cmis:creationDate<= TIMESTAMP  '"+convertedMax+"' "
				+ "AND IN_FOLDER('" + testFolderId + "')";

		logger.info("CMIS Query ready to be performed: " + query);

		ItemIterable<QueryResult> results = session.query(query,false);

		String documentId = "";

		for (Iterator<QueryResult> it = results.iterator(); it.hasNext();) {


			QueryResult r = it.next();

			documentId = String.valueOf(r.getPropertyById(PropertyIds.OBJECT_ID)
					.getFirstValue());

			logger.info("Start the processing of document with id: " + documentId);


			DocumentImpl d = (DocumentImpl) session.getObject(documentId);
			String graphUrl = "<<ERROR GENERETING SPONGER GRAPH>>";
			try{

				if (d.getContentStream() != null) {

					String documentContent = TikaApi.parseToStringExample(d.getContentStream().getStream());

					//					String documentContent = metadataMap.get("documentContent");

					String noSurrogateText = CleanSurrogateChars.cleanTextBySurrogate(documentContent);

					String textRazorApiKey = PropertiesHelper.getProps().getProperty("textRazorApiKey");
					AnalysisResult result = TextRazorApi.testAnalysis(textRazorApiKey, d.getVersionSeriesId(), noSurrogateText);

					//creating the html file with the highlined entity
					HTMLTagEditor.htmlDocumentTagging(d.getVersionSeriesId(), noSurrogateText, result);

					String processedFileLocation = PropertiesHelper.getProps().getProperty("processedFileLocation");
					String idDocument = d.getVersionSeriesId(); //the id document without the version
					graphUrl = processedFileLocation+idDocument+".html";

					int statusSponger = VirtuosoSpongerClient.startSpongerTagging(graphUrl);
					logger.info("Sponger Process completed. Create the graph " + graphUrl);

					//enrich the sponger graph with textrazor entity retrieved
					TextRazorVirtuosoHelper.convertAnalysisInTriples(graphUrl, result.getEntityList());


				} else {
					logger.debug("No content for " + d.getName());
				}
			} catch (IOException | SAXException | TikaException e) {
				e.printStackTrace();
				logger.error("Error occourred", e);			
			} catch (AnalysisException e) {
				e.printStackTrace();
				logger.error("Error occourred", e);
			} catch (ResourceException e){
				e.printStackTrace();
				logger.error("Error occourred", e);
				logger.info("Sponger processing failed on the graph: " +graphUrl + " with the error " + e);
			}
		}

	}


}
