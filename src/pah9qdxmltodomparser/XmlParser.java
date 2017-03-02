/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdxmltodomparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author pears
 */
public class XmlParser {

    private static XmlNode root = new XmlNode();
    protected static DefaultHandler handler = new DefaultHandler() {
        XmlNode currentNode = null;
        Stack<XmlNode> stack = new Stack<>();

        // This is used to ignore searching for a DTD file.
        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
            return new InputSource(new StringReader(""));
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            XmlNode node = new XmlNode();
            node.name = qName;
            int attributeCount = attributes.getLength();
            if (attributeCount > 0) {
                for (int i = 0; i < attributeCount; i++) {
                    node.attributes.put(attributes.getQName(i), attributes.getValue(i));
                }
            }

            stack.add(node);

            if (currentNode != null) {
                if (!currentNode.children.containsKey(node.name)) {
                    currentNode.children.put(node.name, new ArrayList<>());
                }
                currentNode.children.get(node.name).add(node);
            }
            currentNode = node;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            XmlNode poppedNode = stack.pop();
            poppedNode.content = poppedNode.content.trim();
            if (stack.empty()) {
                root.attributes = poppedNode.attributes;
                root.children = poppedNode.children;
                root.content = poppedNode.content;
                root.name = poppedNode.name;
                currentNode = null;
            } else {
                currentNode = stack.peek();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (currentNode != null) {
                currentNode.content += new String(ch, start, length);
            }
        }
    };

    protected static SAXParser setupParser() throws Exception {
        SAXParser saxParser;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            saxParser = factory.newSAXParser();
        } catch (Exception ex) {
            throw ex;
        }
        return saxParser;
    }

    public static XmlNode parse(File xmlFile) throws Exception {

        try {
            setupParser().parse(xmlFile.getAbsoluteFile(), handler);
        } catch (Exception ex) {
            throw ex;
        }
//        System.out.println(xml);
        return root;
    }

    public static XmlNode parse(InputSource xmlSource) throws Exception {
        try {
            setupParser().parse(xmlSource, handler);
        } catch (Exception ex) {
            throw ex;
        }

        return root;
    }
}
