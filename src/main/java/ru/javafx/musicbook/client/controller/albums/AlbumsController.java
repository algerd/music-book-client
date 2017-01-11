
package ru.javafx.musicbook.client.controller.albums;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseAwareController;
import ru.javafx.musicbook.client.controller.paginator.PagedController;
import ru.javafx.musicbook.client.controller.paginator.PaginatorPaneController;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;

@FXMLController(
    value = "/fxml/albums/Albums.fxml",    
    title = "Albums")
@Scope("prototype")
public class AlbumsController extends BaseAwareController implements PagedController  {
    
    private Resource<Album> selectedItem;
    private PagedResources<Resource<Album>> resources; 
    private PaginatorPaneController paginatorPaneController;
    // filter 
    private Resource<Genre> resorceGenre;
    private String searchString = ""; 
    private String sort;
    private String order;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
    private final IntegerProperty minYear = new SimpleIntegerProperty();
    private final IntegerProperty maxYear = new SimpleIntegerProperty();
    
    @Autowired
    private FXMLControllerLoader fxmlLoader;  
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private GenreRepository genreRepository;
    
    @FXML
    private VBox albumTableVBox;
    //filter
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    @Override
    public void setPageValue() { 
        
    }
    
    @FXML
    private void resetFilter() {
        
    }
    
    @FXML
    private void resetSearchField() {
        
    }
    
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) {
        
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        
    }

}
