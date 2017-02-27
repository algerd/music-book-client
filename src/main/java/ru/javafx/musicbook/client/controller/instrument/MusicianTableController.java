
package ru.javafx.musicbook.client.controller.instrument;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.musician.MusicianPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianInstrument;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianInstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_INSTRUMENT_MUSICIAN;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_INSTRUMENT_MUSICIAN;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_INSTRUMENT_MUSICIAN;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/instrument/MusicianTable.fxml")
@Scope("prototype")
public class MusicianTableController extends PagedTableController<Musician> {
    
    protected InstrumentPaneController paneController;
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private MusicianInstrumentRepository musicianInstrumentRepository;
    
    @FXML
    private Label titleLabel;
    @FXML
    private TableColumn<Resource<Musician>, Integer> rankColumn;
    @FXML
    private TableColumn<Resource<Musician>, String> musicianColumn;
    @FXML
    private TableColumn<Resource<Musician>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(InstrumentPaneController paneController) {
        this.paneController = paneController;
        titleLabel.textProperty().bind(this.paneController.getResource().getContent().nameProperty());
        super.initPagedTableController(musicianRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() {       
        rankColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(pagedTable.getItems().indexOf(cellData.getValue()) + 1).asObject());      
        musicianColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());              
    }    
    
    @Override
    protected String createParamString() {
        List<String> params = new ArrayList<>();  
        params.add("instrument.id=" + Helper.getId(paneController.getResource()));
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        return paramStr;
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
    }
    
    private void initRepositoryListeners() {                         
        musicianRepository.clearDeleteListeners(this);           
        musicianRepository.clearUpdateListeners(this);          
        instrumentRepository.clearDeleteListeners(this);           
        instrumentRepository.clearUpdateListeners(this);      
        musicianInstrumentRepository.clearChangeListeners(this);
        
        musicianRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        musicianRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);          
        instrumentRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        instrumentRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);      
        musicianInstrumentRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);   
    }
    
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) throws URISyntaxException { 
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();     
        contextMenuService.clear();        
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                requestViewService.show(MusicianPaneController.class ,selectedItem);
            }           
        }      
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianInstrument musicianInstrument = new MusicianInstrument();
            musicianInstrument.setInstrument(paneController.getResource().getId().getHref());
            contextMenuService.add(ADD_INSTRUMENT_MUSICIAN, new Resource<>(musicianInstrument, new Link("null")));
            if (selectedItem != null) {              
                Resource<MusicianInstrument> resMusicianInstrument = musicianInstrumentRepository.findByMusicianAndInstrument(selectedItem, (Resource<Instrument>) paneController.getResource());
                contextMenuService.add(EDIT_INSTRUMENT_MUSICIAN, resMusicianInstrument);
                contextMenuService.add(DELETE_INSTRUMENT_MUSICIAN, resMusicianInstrument);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);
        }    
    }

}
