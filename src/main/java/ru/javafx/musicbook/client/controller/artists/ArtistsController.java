
package ru.javafx.musicbook.client.controller.artists;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.Params;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.artist.ArtistPaneController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.controller.paginator.Sort.Direction;
import ru.javafx.musicbook.client.controller.paginator.Sort.Order;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.operators.StringOperator;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.*;
import ru.javafx.musicbook.client.service.RequestViewService;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/artists/Artists.fxml",    
    title = "Artists")
@Scope("prototype")
public class ArtistsController extends PagedTableController<Artist> {
      
    private Resource<Genre> resorceGenre;
    private String searchString = "";
    private String sort;
    private String order;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
   
    @Autowired
    private RequestViewService requestViewService;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;   
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
    private ChoiceBox<String> sortChoiceBox;
    @FXML
    private ChoiceBox<String> orderChoiceBox;   
    //table
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
        initFilters();
        super.initPagedTableController(artistRepository);               
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
        resorceGenre = new Resource<>(emptyGenre, new Link(Genre.DEFAULT_GENRE));
        
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
    
    @Override
    protected String createParamString() {
        List<String> params = new ArrayList<>();       
        if (getMinRating() != Params.MIN_RATING || getMaxRating() != Params.MAX_RATING) {
            params.add("rating=" + getMinRating());
            params.add("rating=" + getMaxRating());
        }
        if (!searchString.equals("")) {
            params.add("name=" + StringOperator.STARTS_WITH);
            params.add("name=" + searchString);
        }
        if (!resorceGenre.getContent().getName().equals("All genres")) {
            params.add("genre.id=" + Helper.getId(resorceGenre));
        }
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        logger.info("paramStr :{}", paramStr);
        return paramStr;
    }
            
    @Override
    protected void initPagedTable() {
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(pagedTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );      
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());       
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject()); 
    }
    
    @Override
    protected Sort getSort() {
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
        initFilters();
        paginatorPaneController.initPageComboBox();
    } 
    
    @FXML
    private void resetSearchField() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
    }
    
    private void initRepositoryListeners() {
        artistRepository.clearChangeListeners(this);       
        genreRepository.clearChangeListeners(this);

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
                requestViewService.show(ArtistPaneController.class ,selectedItem);
            }           
        }      
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ARTIST, null);
            // запретить удаление и редактирование записи с id = 1 (Unknown artist)
            if (selectedItem != null && !selectedItem.getId().getHref().equals(Artist.DEFAULT_ARTIST)) {
                contextMenuService.add(EDIT_ARTIST, selectedItem);
                contextMenuService.add(DELETE_ARTIST, selectedItem);                       
            }
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
