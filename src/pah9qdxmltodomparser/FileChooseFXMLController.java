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
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    void ready(Stage stage, Scene scene) {

    }

    @FXML
    private void chooseFile(ActionEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        try {
        XmlParser.parse(selectedFile);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

}
