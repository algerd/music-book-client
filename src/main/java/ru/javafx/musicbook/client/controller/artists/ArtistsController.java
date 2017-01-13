
package ru.javafx.musicbook.client.controller.artists;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
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
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/artists/Artists.fxml",    
    title = "Artists")
@Scope("prototype")
public class ArtistsController extends BaseAwareController implements PagedController {

    private Resource<Artist> selectedItem;
    private PagedResources<Resource<Artist>> resources; 
    private PaginatorPaneController paginatorPaneController;
    // filter properties       
    private Resource<Genre> resorceGenre;
    private String searchString = "";
    private String sort;
    private String order;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
   
    @Autowired
    private FXMLControllerLoader fxmlLoader;  
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;   
    
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
    @FXML
    private ChoiceBox<String> searchChoiceBox;
    @FXML
    private ChoiceBox<String> sortChoiceBox;
    @FXML
    private ChoiceBox<String> orderChoiceBox;   
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
        sort = "Rating";
        order = "Asc";
        initSortAndOrderChoiceBoxes();
        initGenreChoiceBox();
        initArtistsTable();  
        initRepositoryListeners();
        initFilterListeners();     
    }
    
    private void initSortAndOrderChoiceBoxes() {
        sortChoiceBox.getItems().addAll("Rating", "Name");
        sortChoiceBox.getSelectionModel().selectFirst();
        orderChoiceBox.getItems().addAll("Asc", "Desc");
        orderChoiceBox.getSelectionModel().selectFirst();   
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
            genreResources.addAll(genreRepository.findAll().getContent().parallelStream().collect(Collectors.toList()));         
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
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("minrating", getMinRating());
        parameters.put("maxrating", getMaxRating());
        parameters.put("search", searchString);                   
        parameters.putAll(paginatorPaneController.getPaginator().getParameters());
        try {                       
            if (resorceGenre == null || resorceGenre.getContent().getName().equals("All genres")) {
                resources = artistRepository.searchByNameAndRating(parameters);
            } else {
                parameters.put("id_genre", Helper.getId(resorceGenre));
                resources = artistRepository.searchByGenreAndRatingAndName(parameters);
            }          
            paginatorPaneController.getPaginator().setTotalElements((int) resources.getMetadata().getTotalElements());           
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
        paginatorPaneController.getPaginator().setSort(getSort());
        paginatorPaneController.initPaginator(this);
    }
    
    private Sort getSort() {
        return new Sort(new Order(
           order.equals("Asc") ? Direction.ASC : Direction.DESC,
           sort.toLowerCase()
        ));
    }
    
    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating);                      
        resetSearchLabel.setVisible(false);
    }
    
    private void initFilterListeners() {        
        minRating.addListener((ObservableValue, oldValue, newValue)-> filter());
        maxRating.addListener((ObservableValue, oldValue, newValue)-> filter());       
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
    
    private void filter() {
        paginatorPaneController.getPaginator().setSort(getSort());
        setPageValue();
        paginatorPaneController.initPageComboBox();
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
    
    private void initRepositoryListeners() {
        //убрать и повесить слушателя на artistGenreRepository
        artistRepository.clearChangeListeners(this);       
        genreRepository.clearChangeListeners(this);
       
        //убрать и повесить слушателя на artistGenreRepository
        artistRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this); 
        genreRepository.addChangeListener(this::changedGenre, this);
    }
    
    private void changedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        initGenreChoiceBox();
        resetFilter();
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
