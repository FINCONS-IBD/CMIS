package com.fincons.nlp.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class HTMLTagEditor {

	private static final Log logger = LogFactory
			.getLog(HTMLTagEditor.class);
	
	public static void htmlDocumentTagging(String idFile, String documentContent, AnalysisResult result) {
		
	    
//		for(EntityCustom entity : result.getEntityList()){
//			System.out.println(entity.getStartPos() + " - " + entity.getEndPos() + " - " + entity.getMatchedText());
//		}
		
		
		String path = PropertiesHelper.getProps().getProperty("processedFileLocationRealPath");

		String cleanedText = result.getCleanedText();
		
		cleanedText = cleanedText.replaceAll("Â", "");
				
		Set<EntityCustom> entityList = result.getEntityList();
		
		String matchedText, entity_tag, before;
		String end = "</href>";
		
		StringBuilder htmlTaggedText = new StringBuilder(cleanedText);
		
		int extraCharsCounter = 0;
		String realText = "";
		for(EntityCustom entity: entityList){
						
			matchedText = entity.getMatchedText().replaceAll(" ", "_");
			entity_tag = matchedText+"_"+entity.getStartPos()+"_"+entity.getEndPos();
		
			before = "<href id=\""+entity_tag+"\">";
			htmlTaggedText.insert(entity.getStartPos()+extraCharsCounter, before);
						
			extraCharsCounter+= before.length();
			htmlTaggedText.insert(entity.getEndPos()+extraCharsCounter, end);
			extraCharsCounter+= end.length();
		
		}
		
		try{

			File pathOrganization= new File(path);
			if(!pathOrganization.exists()){
				throw new IOException("Directory \"processed\" not Found!");
			}

			File file=new File(path+"/"+idFile+".html"); 

			Document doc = Jsoup.parse(documentContent, "UTF-8");

			Element head = doc.select("head").first(); 

			String scriptHighlineEntity = PropertiesHelper.getProps().getProperty("scriptHighlineEntity");

			head.append(scriptHighlineEntity);

			Element body = doc.select("body").first();
			body.attr("onload", "underlineEntities()");
			body.html(htmlTaggedText.toString());			

			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(doc.toString());
			fileWriter.flush();
			fileWriter.close();

		}catch(IOException e){
			logger.error("Error occurred", e);
			e.printStackTrace();
		}

	}

}
