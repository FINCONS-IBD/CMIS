package com.fincons.nlp.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.SAXException; 

public class TikaApi {

	public static String parseToStringExample(InputStream is) throws IOException, SAXException, TikaException {

		AutoDetectParser parser = new AutoDetectParser();
		ToXMLContentHandler handler = new ToXMLContentHandler();
		Metadata metadata = new Metadata();

		parser.parse(is, handler, metadata);

		return handler.toString();

	}

}
