package ru.ifmo.md.lesson6;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PostParser {
    private InputStream is;
    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String PUB_DATE = "pubDate";
    private static final String LINK = "link";

    public PostParser (InputStream is) {
        this.is = is;
    }

    private String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public ArrayList<PostData> parse() throws IOException, SAXException, ParserConfigurationException {
        ArrayList<PostData> postDataList = new ArrayList<PostData>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        Element element = doc.getDocumentElement();
        element.normalize();

        NodeList nList = element.getElementsByTagName(ITEM);

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                PostData pd = new PostData();
                pd.setPostLink(getValue(LINK, el));
                pd.setPostTitle(getValue(TITLE, el));
                pd.setPostDate(getValue(PUB_DATE, el));
                pd.setPostDescription(getValue(DESCRIPTION, el));
                postDataList.add(pd);
            }
        }

        return postDataList;
    }
}
