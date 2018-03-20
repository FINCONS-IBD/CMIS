package com.fincons.nlp.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CleanSurrogateChars {
	
	private static final Log logger = LogFactory
			.getLog(CleanSurrogateChars.class);

	public static String cleanTextBySurrogate(String textToClean){
		
		StringBuilder fixedText = new StringBuilder();
		StringBuilder surrogateChars = new StringBuilder();
		int rep;
		for (int i = 0; i < textToClean.length(); i++) {
			char c = textToClean.charAt(i);
			
			rep = (int) c;

			if(rep<8000)
				fixedText.append(c);
			else
				surrogateChars.append(c);
				
		}
		
		logger.info("Cleaning task completed. Special chars removed: " + surrogateChars.toString());
		
		return fixedText.toString();
	}

}
