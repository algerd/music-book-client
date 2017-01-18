
package ru.javafx.musicbook.client.controller.albums;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.Params;
import ru.javafx.musicbook.client.controller.BaseAwareController;
import ru.javafx.musicbook.client.controller.paginator.PagedController;
import ru.javafx.musicbook.client.controller.paginator.PaginatorPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_ALBUM;
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
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private GenreRepository genreRepository;
    
    @FXML
    private ChoiceBox<Resource<Genre>> genreChoiceBox;
    @FXML
    private Spinner<Integer> minRatingSpinner; 
    @FXML
    private Spinner<Integer> maxRatingSpinner; 
    @FXML
    private Spinner<Integer> minYearSpinner; 
    @FXML
    private Spinner<Integer> maxYearSpinner;    
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel;   
    @FXML
    private ChoiceBox<String> searchChoiceBox;
    @FXML
    private ChoiceBox<String> sortChoiceBox;
    @FXML
    private ChoiceBox<String> orderChoiceBox;   
    
    // table
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
        sort = "Rating";
        order = "Asc";
        initSortAndOrderChoiceBoxes();
        initGenreChoiceBox();
        initAlbumsTable();
        initRepositoryListeners();
        initRepositoryListeners();
        initFilterListeners();
        initSearchChoiceBox();
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
                        try {  
                            this.setText(artistRepository.getResource(item.getLink("artist").getHref()).getContent().getName());
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }                                          
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
        initFilters();
        paginatorPaneController = (PaginatorPaneController) fxmlLoader.load(PaginatorPaneController.class);
        albumsTableVBox.getChildren().add(paginatorPaneController.getView());
        paginatorPaneController.getPaginator().setSize(5);    
        paginatorPaneController.getPaginator().setSort(getSort());
        paginatorPaneController.initPaginator(this);
    }
    
    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating);                      
                        
        setMinYear(Params.MIN_YEAR);
        setMaxYear(Params.MAX_YEAR);
        Helper.initIntegerSpinner(minYearSpinner, Params.MIN_YEAR, Params.MAX_YEAR, Params.MIN_YEAR, true, minYear);       
        Helper.initIntegerSpinner(maxYearSpinner, Params.MIN_YEAR, Params.MAX_YEAR, Params.MAX_YEAR, true, maxYear);     
        resetSearchLabel.setVisible(false);
    }
    
    @Override
    public void setPageValue() {         
        clearSelectionTable();
        albumsTable.getItems().clear();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("minrating", getMinRating());
        parameters.put("maxrating", getMaxRating());
        parameters.put("minyear", getMinRating());
        parameters.put("maxyear", getMaxRating());       
        parameters.put("search", searchString);                   
        parameters.putAll(paginatorPaneController.getPaginator().getParameters());      
        try {                       
            if (resorceGenre == null || resorceGenre.getContent().getName().equals("All genres")) {
                resources = albumRepository.searchByNameAndRatingAndYear(parameters);
            } else {
                parameters.put("id_genre", Helper.getId(resorceGenre));
                resources = albumRepository.searchByNameAndRatingAndYearAndGenre(parameters);
            }          
            paginatorPaneController.getPaginator().setTotalElements((int) resources.getMetadata().getTotalElements());           
            albumsTable.setItems(FXCollections.observableArrayList(resources.getContent().parallelStream().collect(Collectors.toList())));           
            Helper.setHeightTable(albumsTable, paginatorPaneController.getPaginator().getSize());        
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        } 
    }
    
    private void initGenreChoiceBox() {
        genreChoiceBox.getItems().clear();
        Helper.initResourceChoiceBox(genreChoiceBox); 
           
        Genre emptyGenre = new Genre();
        emptyGenre.setName("All genres");
        resorceGenre = new Resource<>(emptyGenre, new Link("emptyLink"));
        
        List <Resource<Genre>> genreResources = new ArrayList<>();
        genreResources.add(resorceGenre);
        try {
            genreResources.addAll(genreRepository.findAllNames().getContent().parallelStream().collect(Collectors.toList()));         
            genreChoiceBox.getItems().addAll(genreResources);
            genreChoiceBox.getSelectionModel().selectFirst();
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    private void initSortAndOrderChoiceBoxes() {
        sortChoiceBox.getItems().addAll("Rating", "Name");
        sortChoiceBox.getSelectionModel().selectFirst();
        orderChoiceBox.getItems().addAll("Asc", "Desc");
        orderChoiceBox.getSelectionModel().selectFirst();   
    }
    
    private void initSearchChoiceBox() {
        searchChoiceBox.getItems().addAll("Album", "Artist");
        searchChoiceBox.getSelectionModel().selectFirst();
        /*
        searchChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
            (observable, oldValue, newValue) -> repeatFilter()
        );
        */
    }
    
    private Sort getSort() {
        return new Sort(new Sort.Order(
           order.equals("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
           sort.toLowerCase()
        ));
    }
    
    private void initFilterListeners() {
        minRating.addListener((ObservableValue, oldValue, newValue)-> filter());
        maxRating.addListener((ObservableValue, oldValue, newValue)-> filter());
        minYear.addListener((ObservableValue, oldValue, newValue)-> filter());
        maxYear.addListener((ObservableValue, oldValue, newValue)-> filter());
        
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();
            filter();   
        }); 
        genreChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                resorceGenre = newValue;
                filter();
            }
        });
        sortChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            sort = newValue;
            filter();
        });
        orderChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            order = newValue;
            filter();
        });  
    }
    
    private void initRepositoryListeners() {    
        //repositoryService.getArtistRepository().clearChangeListeners(this);               
        albumRepository.clearChangeListeners(this);
        genreRepository.clearChangeListeners(this);
       
        //repositoryService.getArtistRepository().addChangeListener(this::changedArtist, this);               
        albumRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        genreRepository.addChangeListener(this::changedGenre, this);
    }
    
    private void changedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        initGenreChoiceBox();
        resetFilter();
    }
    
    private void filter() {
        paginatorPaneController.getPaginator().setSort(getSort());
        setPageValue();
        paginatorPaneController.initPageComboBox();
    }
      
    private void clearSelectionTable() {
        albumsTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    @FXML
    private void resetFilter() {
        resetSearchField();
        sortChoiceBox.getSelectionModel().selectFirst();
        orderChoiceBox.getSelectionModel().selectFirst();
        genreChoiceBox.getSelectionModel().selectFirst();  
        searchChoiceBox.getSelectionModel().selectFirst();
        initFilters();
        paginatorPaneController.initPageComboBox();
    } 
    
    @FXML
    private void resetSearchField() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
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
                //requestPageService.albumPane(selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ALBUM, null);
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (selectedItem != null && Helper.getId(selectedItem) != 1) {
                contextMenuService.add(EDIT_ALBUM, selectedItem);
                contextMenuService.add(DELETE_ALBUM, selectedItem);                       
            }           
            contextMenuService.show(view, mouseEvent);  
        }
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenuService.add(ADD_ALBUM, null);
            contextMenuService.show(view, mouseEvent);
        } 
    }
    
    public int getMaxYear() {
        return maxYear.get();
    }
    public void setMaxYear(int value) {
        maxYear.set(value);
    }
    public IntegerProperty maxYearProperty() {
        return maxYear;
    }
    
    public int getMinYear() {
        return minYear.get();
    }
    public void setMinYear(int value) {
        minYear.set(value);
    }
    public IntegerProperty minYearProperty() {
        return minYear;
    }
    
    public int getMaxRating() {
        return maxRating.get();
    }
    public void setMaxRating(int value) {
        maxRating.set(value);
    }
    public IntegerProperty maxRatingProperty() {
        return maxRating;
    }
       
    public int getMinRating() {
        return minRating.get();
    }
    public void setMinRating(int value) {
        minRating.set(value);
    }
    public IntegerProperty minRatingProperty() {
        return minRating;
    }

}
