
package ru.javafx.musicbook.client.controller.genres;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseAwareController;
import ru.javafx.musicbook.client.controller.paginator.PagedController;
import ru.javafx.musicbook.client.controller.paginator.PaginatorPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;
import ru.javafx.musicbook.client.repository.AlbumGenreRepository;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistGenreRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.*;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/genres/Genres.fxml",    
    title = "Genres")
@Scope("prototype")
public class GenresController extends BaseAwareController implements PagedController {

    private Resource<Genre> selectedItem;
    private PagedResources<Resource<Genre>> resources; 
    private PaginatorPaneController paginatorPaneController;
    private String searchString = "";
    
    @Autowired
    private FXMLControllerLoader fxmlLoader;  
    @Autowired
    private GenreRepository genreRepository;   
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;   
    @Autowired
    private ArtistGenreRepository artistGenreRepository;
    @Autowired
    private AlbumGenreRepository albumGenreRepository;
    
    @FXML
    private VBox genresTableVBox;
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel;   
    //table
    @FXML
    private TableView<Resource<Genre>> genresTable;
    @FXML
    private TableColumn<Resource<Genre>, String> genreColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> artistsAmountColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> artistsAvRatingColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> albumsAmountColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> albumsAvRatingColumn;
    
    public GenresController() {}
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        initGenresTable();
        initRepositoryListeners();
        initFilterListeners();
    }
    
    @Override
    public void setPageValue() {
        clearSelectionTable();
        genresTable.getItems().clear();
        try {
            resources = genreRepository.searchByName(paginatorPaneController.getPaginator(), searchString);
            genresTable.setItems(FXCollections.observableArrayList(resources.getContent().parallelStream().collect(Collectors.toList())));           
            Helper.setHeightTable(genresTable, paginatorPaneController.getPaginator().getSize(), 2);        
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
    }
    
    private void initGenresTable() {
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());       
        
        artistsAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        artistsAmountColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {  
                            this.setText("" + artistGenreRepository.countArtistsByGenre(item));
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });       
        artistsAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        artistsAvRatingColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) { 
                        try {                                             
                            List<Artist> artists = new ArrayList<>();
                            artistRepository.findByGenre(item).getContent().parallelStream().forEach(
                                artistResource -> artists.add(artistResource.getContent())
                            );        
                            int averageRating = 0;
                            for (Artist artist : artists) {
                                averageRating += artist.getRating();
                            }
                            int count = artists.size();
                            this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / count + 0.5))/ 100.0 : " - "));                            
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
        
        albumsAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        albumsAmountColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {                   
                            this.setText("" + albumGenreRepository.countAlbumsByGenre(item));
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
        albumsAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        albumsAvRatingColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {                                             
                            List<Album> albums = new ArrayList<>();
                            albumRepository.findByGenre(item).getContent().parallelStream().forEach(
                                albumResource -> albums.add(albumResource.getContent())
                            );        
                            int averageRating = 0;
                            for (Album album : albums) {
                                averageRating += album.getRating();
                            }
                            int count = albums.size();
                            this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / count + 0.5))/ 100.0 : " - "));                            
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
                
        genresTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = genresTable.getSelectionModel().getSelectedItem()
        );
        initPaginatorPane();
    }
    
    private void initPaginatorPane() {
        initFilters();
        paginatorPaneController = (PaginatorPaneController) fxmlLoader.load(PaginatorPaneController.class);
        genresTableVBox.getChildren().add(paginatorPaneController.getView());
        paginatorPaneController.getPaginator().setSize(5);
        paginatorPaneController.getPaginator().setSort(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
        paginatorPaneController.initPaginator(this);
    }
    
    private void initFilterListeners() {        
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {           
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();
            filter();   
        });
    }
    
    private void initFilters() {
        resetSearchLabel.setVisible(false);
    }
    
    private void filter() {       
        setPageValue();
        paginatorPaneController.initPageComboBox();
    }
    
    @FXML
    private void resetSearchLabel() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
    }
    
    private void initRepositoryListeners() {                    
       //repositoryService.getAlbumGenreRepository().clearChangeListeners(this);                          
       //repositoryService.getSongGenreRepository().clearChangeListeners(this);                       
       //repositoryService.getMusicianGenreRepository().clearChangeListeners(this);                          
       //repositoryService.getAlbumRepository().clearDeleteListeners(this);  
       //repositoryService.getSongRepository().clearDeleteListeners(this);  
       //repositoryService.getMusicianRepository().clearDeleteListeners(this);
       artistRepository.clearChangeListeners(this);
       genreRepository.clearChangeListeners(this);     
                             
        //repositoryService.getAlbumGenreRepository().addChangeListener(this::changed, this);                          
        //repositoryService.getSongGenreRepository().addChangeListener(this::changed, this);                       
        //repositoryService.getMusicianGenreRepository().addChangeListener(this::changed, this);                          
        //repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);  
        //repositoryService.getSongRepository().addDeleteListener(this::changed, this);  
        //repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);
        artistRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        genreRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);           
    }
    
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) { 
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();     
        contextMenuService.clear();        
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                //GenreEntity genre = repositoryService.getGenreRepository().selectById(selectedItem.getId());
                //requestPageService.genrePane(genre);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_GENRE, null);
            if (selectedItem != null && Helper.getId(selectedItem) > 1) {
                contextMenuService.add(EDIT_GENRE, selectedItem);
                contextMenuService.add(DELETE_GENRE, selectedItem);                       
            }
            contextMenuService.show(view, mouseEvent);       
        }
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_GENRE, null);
            contextMenuService.show(view, mouseEvent);
        }      
    }
    
    private void clearSelectionTable() {
        genresTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    
}
