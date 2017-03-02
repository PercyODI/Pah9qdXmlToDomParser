/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdxmltodomparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pears
 */
public class XmlNode {

    public String name = "";
    public String content = "";
    public HashMap<String, ArrayList<XmlNode>> children;
    public HashMap<String, String> attributes;

    public XmlNode() {
        children = new HashMap<>();
        attributes = new HashMap<>();
    }

    @Override
    public String toString() {
        String returnStr = this.name;
        String namedVal = null;
        if (attributes.containsKey("title")) {
            namedVal = attributes.get("title");
        } else if (attributes.containsKey("name")) {
            namedVal = attributes.get("name");
        } else {
            for (Map.Entry<String, ArrayList<XmlNode>> entry : children.entrySet()) {
                String testEntryName = entry.getKey().toLowerCase();
                if (testEntryName.contains("title") || testEntryName.contains("name")) {
                    namedVal = entry.getValue().get(0).content;
                    break;
                }
            }
        }
        
        if(namedVal != null) {
            returnStr += " (" + namedVal + ")";
        }
        return returnStr;
    }
}
