/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdxmltodomparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author pears
 */
public class XmlParser {

    public static void parse(File xmlFile) throws Exception {
        ArrayList<XmlNode> root = new ArrayList<>();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                
                XmlNode currentNode = null;
                Stack<XmlNode> stack = new Stack<>();

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    XmlNode node = new XmlNode();
                    node.name = qName;
                    int attributeCount = attributes.getLength();
                    if (attributeCount > 0){
                        for(int i = 0; i < attributeCount; i++) {
                            node.attributes.put(attributes.getQName(i), attributes.getValue(i));
                        }
                    }
                    
                    stack.add(node);
                    if(currentNode != null) {
                        if (currentNode.children.containsKey(node.name)) {
                            currentNode.children.get(node.name).add(node);
                        } else {
                            currentNode.children.put(node.name, new ArrayList<>());
                            currentNode.children.get(node.name).add(node);
                        }
                    }
                    currentNode = node;
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    XmlNode poppedNode = stack.pop();
                    poppedNode.content = poppedNode.content.trim();
                    if(stack.empty()) {
                        root.add(poppedNode);
                        currentNode = null;
                    } else {
                        currentNode = stack.peek();
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if(currentNode != null) {
                        currentNode.content += StringEscapeUtils.unescapeHtml4(new String(ch, start, length));
                    }
                }
            };

            saxParser.parse(xmlFile.getAbsoluteFile(), handler);
            System.out.println(root.get(0).name);
        } catch (Exception ex) {
            throw ex;
        }
//        System.out.println(xml);
        return;
    }
}
