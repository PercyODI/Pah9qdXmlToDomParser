/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pah9qdxmltodomparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pears
 */
public class FileChooseFXMLController implements Initializable {
    
    private Stage stage;
    private Scene scene;
    
    @FXML
    private TreeView<XmlNode> treeView;
    
    @FXML
    private TextArea contentBox;
    
    @FXML
    private ListView attributeList;
    private ObservableList<String> attributes;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    void ready(Stage stage, Scene scene) {
        attributes = FXCollections.observableArrayList();
        attributeList.setItems(attributes);

        // Set the attribute list and contentbox from tree selection
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("");
//            try {
                attributes.clear();
                for (Map.Entry<String, String> attribute : newValue.getValue().attributes.entrySet()) {
                    attributes.add(attribute.getKey() + " => " + attribute.getValue());
                }
                
                String contents = newValue.getValue().content;
                contentBox.clear();
                if (contents != null && !contents.isEmpty()) {
                    contentBox.setText(contents);
                }
//            } catch (Exception ex) {
//                displayExceptionAlert(ex);
//            }
        });
    }
    
    @FXML
    private void chooseFile(ActionEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        XmlNode root;
        try {
            root = XmlParser.parse(selectedFile);
        } catch (Exception ex) {
            displayExceptionAlert(ex);
            return;
        }
        
        fillTreeView(root);
    }
    
    private void fillTreeView(XmlNode rootNode) {
        TreeItem<XmlNode> rootItem = new TreeItem<>(rootNode);
        rootItem.expandedProperty().set(true);
        treeView.setRoot(rootItem);
        try {
            addTreeItem(rootNode, rootItem);
        } catch (Exception ex) {
            displayExceptionAlert(ex);
        }

//        boolean hasChildren = true;
//        while(hasChildren) {
//            
//        }
        return;
    }
    
    private void addTreeItem(XmlNode parentNode, TreeItem parentItem) {
        if (parentNode.children == null || parentNode.children.isEmpty()) {
            return;
        }
        
        for (Map.Entry<String, ArrayList<XmlNode>> listOfChildren : parentNode.children.entrySet()) {
            for (XmlNode child : listOfChildren.getValue()) {
                TreeItem<XmlNode> treeItem = new TreeItem<>(child);    //Create a new node
                addTreeItem(child, treeItem);                               //Add all of its children to item
                parentItem.getChildren().add(treeItem);                        //Add this to its parent
            }
        }
        
        return;
    }
    
    public void displayExceptionAlert(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("An Exception Occurred!");
        alert.setContentText("An exception occurred.  View the exception information below by clicking Show Details.");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        
        Label label = new Label("The exception stacktrace was:");
        
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        
        alert.showAndWait();
    }
    
}
