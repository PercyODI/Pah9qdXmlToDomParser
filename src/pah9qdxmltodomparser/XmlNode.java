/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdxmltodomparser;

import java.util.ArrayList;
import java.util.HashMap;

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
            return this.name;
        }
    }
