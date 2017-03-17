package ru.javafx.musicbook.client.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javafx.musicbook.client.controller.explorer.ExplorerController;
import ru.javafx.musicbook.client.fxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.service.RequestViewService;

@FXMLController
public class LeftBarController extends BaseFxmlController {
       
    @FXML
    private AnchorPane leftBar;
    //@FXML
    //private VBox leftBarVBox;
    @FXML
    private ExplorerController includedExplorerController;
    
    @Autowired
    private MainController parentController;
    
    @Autowired
    private RequestViewService requestViewService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        super.setView(leftBar);
    }
    /*
    public void show(BaseFxmlController controller) {
        leftBarVBox.getChildren().add(controller.getView());
    }
    */  
}
