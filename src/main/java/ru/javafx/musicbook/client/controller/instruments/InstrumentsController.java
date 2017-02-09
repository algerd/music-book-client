
package ru.javafx.musicbook.client.controller.instruments;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.operators.StringOperator;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_INSTRUMENT;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_INSTRUMENT;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_INSTRUMENT;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/instruments/Instruments.fxml",    
    title = "Instruments")
@Scope("prototype")
public class InstrumentsController extends BaseAwareController implements PagedController {
    
    private Resource<Instrument> selectedItem;
    private PagedResources<Resource<Instrument>> resources;
    private PaginatorPaneController paginatorPaneController;
    private String searchString = "";
    
    @Autowired
    private FXMLControllerLoader fxmlLoader;  
    @Autowired
    private InstrumentRepository instrumentRepository;  
    
    @FXML
    private VBox instrumentsTableVBox;
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel; 
    //table
    @FXML
    private TableView<Resource<Instrument>> instrumentsTable;
    @FXML
    private TableColumn<Resource<Instrument>, String> instrumentColumn;
    @FXML
    private TableColumn<Resource<Instrument>, Resource<Instrument>> numberOfMusiciansColumn;
    @FXML
    private TableColumn<Resource<Instrument>, Resource<Instrument>> averageRatingColumn;
    
    public InstrumentsController() {}
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        initInstrumentsTable();
        initFilters();
        initPaginatorPane();
        initRepositoryListeners();
        initFilterListeners();
    }
    
    @Override
    public void setPageValue() {
        clearSelectionTable();
        instrumentsTable.getItems().clear();
        try {
            resources = instrumentRepository.getPagedResources(createParamString());
            paginatorPaneController.getPaginator().setTotalElements((int) resources.getMetadata().getTotalElements());
            instrumentsTable.setItems(FXCollections.observableArrayList(resources.getContent().parallelStream().collect(Collectors.toList())));           
            Helper.setHeightTable(instrumentsTable, paginatorPaneController.getPaginator().getSize());        
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
    }
    
    private String createParamString() {
        List<String> params = new ArrayList<>();              
        if (!searchString.equals("")) {
            params.add("name=" + StringOperator.STARTS_WITH_IGNORE_CASE);
            params.add("name=" + searchString);
        }
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        logger.info("paramStr :{}", paramStr);
        return paramStr;
    }
    
    private void initInstrumentsTable() { 
        instrumentColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());       
        numberOfMusiciansColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        numberOfMusiciansColumn.setCellFactory(col -> {
            TableCell<Resource<Instrument>, Resource<Instrument>> cell = new TableCell<Resource<Instrument>, Resource<Instrument>>() {
                @Override
                public void updateItem(Resource<Instrument> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        //this.setText("" + repositoryService.getMusicianInstrumentRepository().countMusicianInstrumentByInstrument(item));                   
                    }
                }
            };
			return cell;
        });
        averageRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        averageRatingColumn.setCellFactory(col -> {
            TableCell<Resource<Instrument>, Resource<Instrument>> cell = new TableCell<Resource<Instrument>, Resource<Instrument>>() {
                @Override
                public void updateItem(Resource<Instrument> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        /*
                        List<MusicianInstrumentEntity> musicianInstruments = repositoryService.getMusicianInstrumentRepository().selectMusicianInstrumentByInstrument(item);
                        int averageRating = 0;
                        for (MusicianInstrumentEntity musicianInstrument : musicianInstruments) {
                            averageRating += musicianInstrument.getMusician().getRating();
                        }
                        int countMusicians = musicianInstruments.size();
                        this.setText("" + ((countMusicians != 0) ? ((int) (100.0 * averageRating / musicianInstruments.size() + 0.5))/ 100.0 : ""));
                        */
                    }
                }
            };
			return cell;
        });
        
        instrumentsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = instrumentsTable.getSelectionModel().getSelectedItem()
        );
    }
    
    private void initPaginatorPane() {
        paginatorPaneController = (PaginatorPaneController) fxmlLoader.load(PaginatorPaneController.class);
        instrumentsTableVBox.getChildren().add(paginatorPaneController.getView());
        paginatorPaneController.getPaginator().setSize(5);
        paginatorPaneController.getPaginator().setSort(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
        paginatorPaneController.initPaginator(this);
    }
    
    private void initFilters() {
        resetSearchLabel.setVisible(false);
    }
    
    private void initFilterListeners() {        
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {           
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();
            filter();   
        });
    }
    
    private void filter() {       
        setPageValue();
        paginatorPaneController.initPageComboBox();
    }
    
    private void initRepositoryListeners() {       
        //clear listeners
        instrumentRepository.clearChangeListeners(this);  
        //repositoryService.getMusicianInstrumentRepository().clearChangeListeners(this);               
        //repositoryService.getMusicianRepository().clearDeleteListeners(this);                         
        
        //add listeners
        instrumentRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        //repositoryService.getMusicianInstrumentRepository().addChangeListener(this::changed, this);
        //repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);        
    }
    
    private void clearSelectionTable() {
        instrumentsTable.getSelectionModel().clearSelection();
        selectedItem = null;
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
                //GenreEntity genre = repositoryService.getGenreRepository().selectById(selectedItem.getId());
                //requestPageService.genrePane(genre);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_INSTRUMENT, null);
            if (selectedItem != null && !selectedItem.getId().getHref().equals(Instrument.DEFAULT_INSTRUMENT)) {
                contextMenuService.add(EDIT_INSTRUMENT, selectedItem);
                contextMenuService.add(DELETE_INSTRUMENT, selectedItem);                       
            }
            contextMenuService.show(view, mouseEvent);       
        }
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_INSTRUMENT, null);
            contextMenuService.show(view, mouseEvent);
        }      
    }

}
