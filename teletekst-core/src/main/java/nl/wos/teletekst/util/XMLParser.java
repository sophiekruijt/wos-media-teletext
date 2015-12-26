package nl.wos.teletekst.util;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public class XMLParser {

    public static Document XMLParser(String text) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input =  new ByteArrayInputStream(text.getBytes("UTF-8"));
        return builder.parse(input);
    }
}
