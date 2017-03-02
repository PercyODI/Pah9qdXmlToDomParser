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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
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
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.xml.sax.InputSource;

/**
 * FXML Controller class
 *
 * @author pears
 */
public class ParseViewController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private VBox vBox;

    @FXML
    private TreeView<XmlNode> treeView;

    @FXML
    private WebView contentView;
    private WebEngine webEngine;

    @FXML
    private ListView attributeList;
    private ObservableList<String> attributes;

    @FXML
    private TextArea helpText;

    private final String DEMOURL = "http://rss.msn.com";

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

        webEngine = contentView.getEngine();

        // Set the attribute list and contentbox from tree selection
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                // Clear UI of previous selection
                attributes.clear();
                webEngine.loadContent("");
                
                // Changing files can result in newValue being null. Return if newValue is null
                if(newValue == null)
                    return;
                
                if (newValue.getValue().attributes.isEmpty()) {
                    attributes.add("--No Attributes For Selected Item--");
                } else {
                    for (Map.Entry<String, String> attribute : newValue.getValue().attributes.entrySet()) {
                        attributes.add(attribute.getKey() + " => " + attribute.getValue());
                    }
                }

                String contents = newValue.getValue().content;
                if (contents != null && !contents.isEmpty()) {
                    webEngine.loadContent(contents);
                } else {
                    webEngine.loadContent("<small>--No Content For Selected Item--</small>");
                }
            } catch (Exception ex) {
                displayExceptionAlert(ex);
            }
        });
    }

    @FXML
    private void chooseFile(ActionEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            return;
        }

        XmlNode root;
        try {
            root = XmlParser.parse(selectedFile);
        } catch (Exception ex) {
            displayExceptionAlert(ex);
            return;
        }

        fillTreeView(root);
        vBox.getChildren().remove(helpText);
    }

    @FXML
    private void runDemoXml(ActionEvent event) throws MalformedURLException, IOException {
        XmlNode root;
        try {
            root = XmlParser.parse(new InputSource(new URL(DEMOURL).openStream()));
        } catch (Exception ex) {
            displayExceptionAlert(ex);
            return;
        }

        fillTreeView(root);
        vBox.getChildren().remove(helpText);
    }

    private void fillTreeView(XmlNode rootNode) {
        TreeItem<XmlNode> rootItem = new TreeItem<>(rootNode);
        rootItem.expandedProperty().set(true);
        treeView.setRoot(rootItem);
        try {
            addTreeItem(rootNode, rootItem); // Recursively adds all children in their correct places
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
                addTreeItem(child, treeItem);                          //Add all of its children to item
                parentItem.getChildren().add(treeItem);                //Add this to its parent
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
