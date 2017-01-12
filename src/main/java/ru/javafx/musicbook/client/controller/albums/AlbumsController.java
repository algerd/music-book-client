
package ru.javafx.musicbook.client.controller.albums;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import ru.javafx.musicbook.client.utils.Helper;

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
    private VBox albumsTableVBox;
    @FXML
    private TableView<Resource<Album>> albumsTable;
    @FXML
    private TableColumn<Resource<Album>, Integer> rankColumn;
    @FXML
    private TableColumn<Resource<Album>, Resource<Album>> artistColumn;
    @FXML
    private TableColumn<Resource<Album>, String> albumColumn;
    @FXML
    private TableColumn<Resource<Album>, Integer> yearColumn;
    @FXML
    private TableColumn<Resource<Album>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initAlbumsTable();
    }
    
    private void initAlbumsTable() { 
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(albumsTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        ); 
        
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());       
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().yearProperty().asObject());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject()); 
        
        artistColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
		artistColumn.setCellFactory(col -> {
			TableCell<Resource<Album>, Resource<Album>> cell = new TableCell<Resource<Album>, Resource<Album>>() {
                @Override
                public void updateItem(Resource<Album> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        //ArtistEntity artist = repositoryService.getArtistRepository().selectById(item.getId_artist());                 
                        //this.setText(artist.getName());                                          
                    }
                }
            };
			return cell;
		});
        
        albumsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = albumsTable.getSelectionModel().getSelectedItem()
        );
        initPaginatorPane();
    }
    
    private void initPaginatorPane() {
        //initFilters();
        paginatorPaneController = (PaginatorPaneController) fxmlLoader.load(PaginatorPaneController.class);
        albumsTableVBox.getChildren().add(paginatorPaneController.getView());
        paginatorPaneController.getPaginator().setSize(5);    
        //paginatorPaneController.getPaginator().setSort(getSort());
        paginatorPaneController.initPaginator(this);
    }
    
    @Override
    public void setPageValue() { 
        /*
        clearSelectionTable();
        albumsTable.getItems().clear();
        try {         
            resources = albumRepository.searchByGenreAndRatingAndName(paginatorPaneController.getPaginator(), getMinRating(), getMaxRating(), searchString, resorceGenre);
            albumsTable.setItems(FXCollections.observableArrayList(resources.getContent().parallelStream().collect(Collectors.toList())));           
            Helper.setHeightTable(albumsTable, paginatorPaneController.getPaginator().getSize());        
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        } 
        */
    }
    
    private void clearSelectionTable() {
        albumsTable.getSelectionModel().clearSelection();
        selectedItem = null;
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
