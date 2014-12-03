package ru.ifmo.md.lesson6;

import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

public class SAXXMLParser {
    public static ArrayList<PostItem> parse(InputStream is) {
        ArrayList<PostItem> posts = null;
        try {
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            SAXXMLHandler saxHandler = new SAXXMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            xmlReader.parse(new InputSource(is));
            // get the `Posts list`
            posts = saxHandler.getPosts();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // return Posts list
        return posts;
    }
}
