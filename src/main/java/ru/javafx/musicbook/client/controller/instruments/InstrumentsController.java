
package ru.javafx.musicbook.client.controller.instruments;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianInstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.operators.StringOperator;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_INSTRUMENT;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_INSTRUMENT;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_INSTRUMENT;

@FXMLController(
    value = "/fxml/instruments/Instruments.fxml",    
    title = "Instruments")
@Scope("prototype")
public class InstrumentsController extends PagedTableController<Instrument> {

    private String searchString = "";
    
    @Autowired
    private InstrumentRepository instrumentRepository; 
    @Autowired
    private MusicianRepository musicianRepository; 
    @Autowired
    private MusicianInstrumentRepository musicianInstrumentRepository;

    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel; 
    //table
    @FXML
    private TableColumn<Resource<Instrument>, String> instrumentColumn;
    @FXML
    private TableColumn<Resource<Instrument>, Resource<Instrument>> numberOfMusiciansColumn;
    @FXML
    private TableColumn<Resource<Instrument>, Resource<Instrument>> averageRatingColumn;
    
    public InstrumentsController() {}
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        initFilters();
        super.initPagedTableController(instrumentRepository); 
        initRepositoryListeners();
        initFilterListeners();
    }
    
    @Override
    protected String createParamString() {
        List<String> params = new ArrayList<>();              
        if (!searchString.equals("")) {
            params.add("name=" + StringOperator.STARTS_WITH_IGNORE_CASE);
            params.add("name=" + searchString);
        }
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        //logger.info("paramStr :{}", paramStr);
        return paramStr;
    }
    
    @Override
    protected void initPagedTable() { 
        instrumentColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());       
        numberOfMusiciansColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        numberOfMusiciansColumn.setCellFactory(col -> {
            TableCell<Resource<Instrument>, Resource<Instrument>> cell = new TableCell<Resource<Instrument>, Resource<Instrument>>() {
                @Override
                public void updateItem(Resource<Instrument> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                                               
                        try {  
                            this.setText("" + musicianInstrumentRepository.countMusiciansByInstrument(item));
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
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
                         try {                                             
                            List<Musician> musicians = new ArrayList<>();
                            musicianRepository.findByInstrument(item).getContent().parallelStream().forEach(
                                instrumentResource -> musicians.add(instrumentResource.getContent())
                            );        
                            int averageRating = 0;
                            for (Musician musician : musicians) {
                                averageRating += musician.getRating();
                            }
                            int count = musicians.size();
                            this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / count + 0.5))/ 100.0 : " - "));                            
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
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
        musicianRepository.clearChangeListeners(this);                         
        
        //add listeners
        instrumentRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        musicianRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);       
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
