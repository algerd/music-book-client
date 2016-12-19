
package ru.javafx.musicbook.client.controller.artists;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.Params;
import ru.javafx.musicbook.client.controller.BaseAwareController;
import ru.javafx.musicbook.client.controller.paginator.PaginatorPaneController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;
import ru.javafx.musicbook.client.controller.paginator.PagedController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.controller.paginator.Sort.Direction;
import ru.javafx.musicbook.client.controller.paginator.Sort.Order;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.*;
import ru.javafx.musicbook.client.service.RequestService;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/artists/Artists.fxml",    
    title = "Artists")
@Scope("prototype")
public class ArtistsController extends BaseAwareController implements PagedController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private Resource<Artist> selectedItem;
    private PagedResources<Resource<Artist>> resources; 
    private PaginatorPaneController paginatorPaneController;
    // filter properties       
    //private GenreEntity genre;
    private String searchString = "";
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
   
    @Autowired
    private FXMLControllerLoader fxmlLoader;  
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;   
    @Autowired
    private RequestService requestService;
    
    @FXML
    private VBox artistsTableVBox;
    //filter
    @FXML
    private ChoiceBox<Resource<Genre>> genreChoiceBox;
    @FXML
    private Spinner<Integer> minRatingSpinner; 
    @FXML
    private Spinner<Integer> maxRatingSpinner; 
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel; 
    //table
    @FXML
    private TableView<Resource<Artist>> artistsTable;
    @FXML
    private TableColumn<Resource<Artist>, Integer> rankColumn;
    @FXML
    private TableColumn<Resource<Artist>, String> artistColumn;
    @FXML
    private TableColumn<Resource<Artist>, Integer> ratingColumn; 
    
    public ArtistsController() {}
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        initArtistsTable();  
        initFilterListeners();
        initGenreChoiceBox();
    }
    
    private void initGenreChoiceBox() {
        Helper.initResourceChoiceBox(genreChoiceBox); 
        List <Resource<Genre>> genreResources = new ArrayList<>();
        
        Genre emptyGenre = new Genre();
        emptyGenre.setName("All genres");
        Resource<Genre> emptyResource = new Resource<>(emptyGenre, new Link("emptyLink"));
        genreResources.add(emptyResource);
        try {
            genreResources.addAll(genreRepository.getAll().getContent().parallelStream().collect(Collectors.toList()));         
            genreResources.sort(Comparator.comparing(genreResource -> genreResource.getContent().getName()));
            genreChoiceBox.getItems().addAll(genreResources);
            genreChoiceBox.getSelectionModel().selectFirst();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void setPageValue() {  
        clearSelectionTable();
        artistsTable.getItems().clear();
        try {
            resources = artistRepository.getArtists(paginatorPaneController.getPaginator(), getMinRating(), getMaxRating(), searchString);
            artistsTable.setItems(FXCollections.observableArrayList(resources.getContent().parallelStream().collect(Collectors.toList())));           
            Helper.setHeightTable(artistsTable, paginatorPaneController.getPaginator().getSize());        
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
    }
    
    private void initArtistsTable() { 
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(artistsTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );      
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());       
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject()); 
        
        artistsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = artistsTable.getSelectionModel().getSelectedItem()
        );
        initPaginatorPane();
    }
    
    private void initPaginatorPane() {
        initFilters();
        paginatorPaneController = (PaginatorPaneController) fxmlLoader.load(PaginatorPaneController.class);
        artistsTableVBox.getChildren().add(paginatorPaneController.getView());
        paginatorPaneController.getPaginator().setSize(5);
        paginatorPaneController.getPaginator().setSort(new Sort(new Order(Direction.ASC, "rating")));
        paginatorPaneController.initPaginator(this);
    }
    
    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating);                      
        resetSearchLabel.setVisible(false);
    }
    
    private void initFilterListeners() {        
        minRating.addListener((ObservableValue, oldValue, newValue)-> {
            filter();
        });
        maxRating.addListener((ObservableValue, oldValue, newValue)-> filter());       
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {           
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();
            filter();   
        });       
        /*
        genreChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                genre = newValue;
                filter();
            }
        });
        */
    }
    
    private void filter() {
        //logger.info("minRating {}", minRating.get());
        //logger.info("maxRating {}", maxRating.get());
        //logger.info("searchField {}", searchField.getText());        
        setPageValue();
        paginatorPaneController.initPageComboBox();
    }
    
    @FXML
    private void resetFilter() {
        resetSearchField();
        //genreChoiceBox.getSelectionModel().selectFirst();
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
                //requestPageService.artistPane(selectedItem);
            }           
        }      
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ARTIST, null);
            // запретить удаление и редактирование записи с id = 1 (Unknown artist)
            //if (selectedItem != null && selectedItem.getId() != 1) {
                contextMenuService.add(EDIT_ARTIST, selectedItem);
                contextMenuService.add(DELETE_ARTIST, selectedItem);                       
            //}
            contextMenuService.show(view, mouseEvent);         
        }    
    }
    
    /**
     * При ПКМ по странице артиста показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ARTIST, null);
            contextMenuService.show(view, mouseEvent);
        }      
    }
    
    private void clearSelectionTable() {
        artistsTable.getSelectionModel().clearSelection();
        selectedItem = null;
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
