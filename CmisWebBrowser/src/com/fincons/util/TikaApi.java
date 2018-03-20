package com.fincons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.serialization.JsonMetadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaApi {

	public static Map<String,String> parseToStringExample(InputStream is) throws IOException, SAXException, TikaException {
		ContentHandler handler = new ToXMLContentHandler();

		//		OriginalBodyContentHandler handler = new OriginalBodyContentHandler();

		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();
		parser.parse(is, handler, metadata);


		StringWriter writer = new StringWriter();
		JsonMetadata.toJson(metadata, writer);

		String metatadaJSON = writer.toString();

		Map<String,String> resultMap = new HashMap<String,String>();

		resultMap =(JSONObject)JSONValue.parse(metatadaJSON);

		System.out.println(handler.toString());
		resultMap.put("documentContent", handler.toString());

		return resultMap;

	}

	public static String parseExample() {
		AutoDetectParser parser = new AutoDetectParser();
		ToXMLContentHandler handler = new ToXMLContentHandler();
		Metadata metadata = new Metadata();
		try{
			File initialFile = new File("C:/cmis-article.pdf");
			InputStream targetStream = new FileInputStream(initialFile);
			parser.parse(targetStream, handler, metadata);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return handler.toString();
	}

	public static void main(String[] args) {

		String parsed = parseExample();
		System.out.println(parsed);

	}

}
