package ru.javafx.musicbook.client.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javafx.musicbook.client.fxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.service.RequestViewService;

@FXMLController
public class LeftBarController extends BaseFxmlController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @FXML
    private AnchorPane leftBar;
    
    @Autowired
    private MainController parentController;
    
    @Autowired
    private RequestViewService requestViewService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        super.setView(leftBar);
    }
    
   
      
}
