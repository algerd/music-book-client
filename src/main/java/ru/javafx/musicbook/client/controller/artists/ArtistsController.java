
package ru.javafx.musicbook.client.controller.artists;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpHeaders;
import ru.javafx.musicbook.client.SessionManager;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.jfxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.jfxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.service.RequestService;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/artists/Artists.fxml",    
    title = "Artists")
@Scope("prototype")
public class ArtistsController extends BaseFxmlController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Artist selectedItem;
    private List<Artist> artists;
    
    @Autowired
    private ArtistRepository artistRepository;
    
    @Autowired
    private RequestService requestService;
       
    //table
    @FXML
    private TableView<Artist> artistsTable;
    @FXML
    private TableColumn<Artist, Integer> rankColumn;
    @FXML
    private TableColumn<Artist, String> artistColumn;
    @FXML
    private TableColumn<Artist, Integer> ratingColumn; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {    
        initArtistsTable();
        setTableValue();
    } 
    
    private void initArtistsTable() { 
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(artistsTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );      
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());       
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject()); 
        
        // Добавить слушателя на выбор элемента в таблице.
        artistsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = artistsTable.getSelectionModel().getSelectedItem()
        );
    }
    
    public void setTableValue() {
        requestGetArtists();
        artistsTable.setItems(FXCollections.observableArrayList(artists));      
        //sort();       
        Helper.setHeightTable(artistsTable, 10);  
    }
    
    private void requestGetArtists() {    
        try { 
            PagedResources<Resource<Artist>> resources = artistRepository.getArtists();
            
            PagedResources.PageMetadata metadata = resources.getMetadata();
            logger.info("Got {} of {} artists: ", resources.getContent().size(), metadata.getTotalElements());
            artists = resources.getContent().stream().map(Resource::getContent).collect(Collectors.toList());                     
        } 
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }

}
