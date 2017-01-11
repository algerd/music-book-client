package ru.javafx.musicbook.client.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javafx.musicbook.client.controller.albums.AlbumsController;
import ru.javafx.musicbook.client.controller.artists.ArtistsController;
import ru.javafx.musicbook.client.controller.genres.GenresController;
import ru.javafx.musicbook.client.fxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.service.RequestViewService;

@FXMLController
public class TopBarController extends BaseFxmlController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
      
    @Autowired
    private MainController parentController;
    
    @Autowired
    private RequestViewService requestViewService;
    
    @FXML
    private AnchorPane topBar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.setView(topBar);
    }
    
    @FXML
    private void showArtists() {
        requestViewService.show(ArtistsController.class);
    }
    
    @FXML
    private void showAlbums() {
        requestViewService.show(AlbumsController.class);
    }
    
    @FXML
    private void showGenres() {
        requestViewService.show(GenresController.class);
    }
     
}
