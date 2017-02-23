
package ru.javafx.musicbook.client.controller.musicians;

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
import ru.javafx.musicbook.client.controller.musician.MusicianPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.operators.StringOperator;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_MUSICIAN;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_MUSICIAN;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_MUSICIAN;
import ru.javafx.musicbook.client.service.RequestViewService;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/musicians/Musicians.fxml",    
    title = "Musicians")
@Scope("prototype")
public class MusiciansController extends PagedTableController<Musician> {

    private Resource<Genre> resorceGenre;
    private Resource<Instrument> resorceInstrument;
    private String searchString = ""; 
    private String sort;
    private String order;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();

    @Autowired
    private RequestViewService requestViewService;
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    
    // filters:
    @FXML
    private Spinner<Integer> minRatingSpinner; 
    @FXML
    private Spinner<Integer> maxRatingSpinner;
    @FXML
    private ChoiceBox<Resource<Genre>> genreChoiceBox;
    @FXML
    private ChoiceBox<Resource<Instrument>> instrumentChoiceBox;        
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel;   
    @FXML
    private ChoiceBox<String> sortChoiceBox;
    @FXML
    private ChoiceBox<String> orderChoiceBox;
    /* ************ musiciansTable *************** */   
    @FXML
    private TableColumn<Resource<Musician>, Integer> rankColumn;  
    @FXML
    private TableColumn<Resource<Musician>, String> nameColumn;
    @FXML
    private TableColumn<Resource<Musician>, String> dobColumn;
    @FXML
    private TableColumn<Resource<Musician>, String> dodColumn;
    @FXML
    private TableColumn<Resource<Musician>, String> countryColumn;  
    @FXML
    private TableColumn<Resource<Musician>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sort = "Rating";
        order = "Asc";
        initSortAndOrderChoiceBoxes();
        initGenreChoiceBox();
        initInstrumentChoiceBox();
        initFilters();
        super.initPagedTableController(musicianRepository); 
        initRepositoryListeners();
        initFilterListeners();      
    }
    
    private void initSortAndOrderChoiceBoxes() {
        sortChoiceBox.getItems().addAll("Rating", "Name", "Country");
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
        
        List<Resource<Genre>> genreResources = new ArrayList<>();
        genreResources.add(resorceGenre);
        try {
            genreResources.addAll(genreRepository.findAllNames().getContent().parallelStream().collect(Collectors.toList()));         
            genreChoiceBox.getItems().addAll(genreResources);
            genreChoiceBox.getSelectionModel().selectFirst();
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    private void initInstrumentChoiceBox() {
        instrumentChoiceBox.getItems().clear();
        Helper.initResourceChoiceBox(instrumentChoiceBox); 
           
        Instrument emptyInstrument = new Instrument();
        emptyInstrument.setName("All instruments");
        resorceInstrument = new Resource<>(emptyInstrument, new Link(Instrument.DEFAULT_INSTRUMENT));
        
        List<Resource<Instrument>> instrumentResources = new ArrayList<>();
        instrumentResources.add(resorceInstrument);
        try {
            instrumentResources.addAll(instrumentRepository.findAllNames().getContent().parallelStream().collect(Collectors.toList()));         
            instrumentChoiceBox.getItems().addAll(instrumentResources);
            instrumentChoiceBox.getSelectionModel().selectFirst();
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating);                      
        resetSearchLabel.setVisible(false);
    }
    
    @Override
    protected void initPagedTable() { 
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(pagedTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        dobColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().dateOfBirthProperty());
        dodColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().dateOfDeathProperty());
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().countryProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());        
    }
   
    private void initRepositoryListeners() {
        //clear listeners
        musicianRepository.clearChangeListeners(this);                                                
        genreRepository.clearChangeListeners(this); 
        instrumentRepository.clearChangeListeners(this);
        
        //add listeners
        musicianRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);       
        genreRepository.addChangeListener(this::changedGenre, this);
        instrumentRepository.addChangeListener(this::changedInstrument, this);
    }
     
    private void changedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        initGenreChoiceBox();
        resetFilter();
    } 
    
    private void changedInstrument(ObservableValue observable, Object oldVal, Object newVal) {
        initInstrumentChoiceBox();
        resetFilter();
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
        instrumentChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                resorceInstrument = newValue;                            
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
        if (!resorceInstrument.getContent().getName().equals("All instruments")) {
            params.add("instrument.id=" + Helper.getId(resorceInstrument));
        }
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        logger.info("paramStr :{}", paramStr);
        return paramStr;
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
        instrumentChoiceBox.getSelectionModel().selectFirst();  
        initFilters();
        paginatorPaneController.initPageComboBox();
    } 
    
    @FXML
    private void resetSearchField() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(
           order.equals("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
           sort.toLowerCase()
        ));
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
                requestViewService.show(MusicianPaneController.class, selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_MUSICIAN, null);
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (selectedItem != null && !selectedItem.getId().getHref().equals(Musician.DEFAULT_MUSICIAN)) {
                contextMenuService.add(EDIT_MUSICIAN, selectedItem);
                contextMenuService.add(DELETE_MUSICIAN, selectedItem);  
            }
            contextMenuService.show(view, mouseEvent);  
        }
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenuService.add(ADD_MUSICIAN, null);
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
