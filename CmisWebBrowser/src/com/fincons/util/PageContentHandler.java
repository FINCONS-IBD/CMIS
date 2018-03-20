package com.fincons.util;

import org.apache.tika.sax.ToTextContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

public class PageContentHandler extends ToXMLContentHandler {
    final static private String pageTag = "div";
    final static private String pageClass = "page";

    /**
     * StringBuilder of current page
     */
    private StringBuilder builder;

    /**
     * page list - setting the initial capacity to 500 will enhance speed by a tiny bit up to 500 bits, but will require
     * some RAM
     */
    private List<String> pages = new ArrayList<>(500);

    /**
     * flag telling to compress text information by stripping whitespace?
     */
    private final boolean compress;

    /**
     * Default constructor
     */
    public PageContentHandler() {
        this.compress = true;
    }

    /**
     * Constructor
     * @param compress text information by stripping whitespace?
     */
    public PageContentHandler(boolean compress) {
        this.compress = compress;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (pageTag.endsWith(qName) && pageClass.equals(atts.getValue("class")))
            startPage();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (pageTag.endsWith(qName))
            endPage();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // append data
        if (length > 0 && builder != null) {
            builder.append(ch);
        }
    }

    protected void startPage() throws SAXException {
        builder = new StringBuilder();
    }

    protected void endPage() throws SAXException {
        String page = builder.toString();

        // if compression has been turned on, compact whitespace and trim string
        if (compress)
            page = page.replaceAll("\\s+", " ").trim();

        // add to page list at end of page list
        pages.add(page);
    }

    /**
     * @return all extracted pages
     */
    public List<String> getPages() {
        return pages;
    }
}