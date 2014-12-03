package ru.ifmo.md.lesson6;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class SAXXMLHandler extends DefaultHandler {
    enum Tags {
        ITEM, LINK, TITLE, PUB_DATE, DESCRIPTION, EMPTY
    }

    private ArrayList<PostItem> posts;
    private Tags tempVal;
    private PostItem tempItem = new PostItem();

    private static final String ITEM = "item";
    private static final String LINK = "link";
    private static final String TITLE = "title";
    private static final String PUB_DATE = "pubDate";
    private static final String DESCRIPTION = "description";


    public SAXXMLHandler() {
        posts = new ArrayList<PostItem>();
    }

    public ArrayList<PostItem> getPosts() {
        return posts;
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase(ITEM)) {
            tempItem = new PostItem();
            tempVal = Tags.EMPTY;
        } else if (qName.equalsIgnoreCase(LINK)) {
            tempVal = Tags.LINK;
        } else if (qName.equalsIgnoreCase(TITLE)) {
            tempVal = Tags.TITLE;
        } else if (qName.equalsIgnoreCase(PUB_DATE)) {
            tempVal = Tags.PUB_DATE;
        } else if (qName.equalsIgnoreCase(DESCRIPTION)) {
            tempVal = Tags.DESCRIPTION;
        } else {
            tempVal = Tags.EMPTY;
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        String content = new String(ch, start, length);
        if (!content.isEmpty() && !content.matches("\\s*")) {
            switch (tempVal) {
                case LINK:
                    tempItem.appPostLink(content);
                    break;
                case TITLE:
                    tempItem.appPostTitle(content);
                    break;
                case PUB_DATE:
                    tempItem.appPostDate(content);
                    break;
                case DESCRIPTION:
                    tempItem.appPostDescription(content);
                    break;
            }
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("item")) {
            if (tempItem.getPostDescription() != null && tempItem.getPostTitle() != null)
                posts.add(tempItem);
        } else {
            tempVal = Tags.EMPTY;
        }
    }
}
