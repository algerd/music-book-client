package ru.javafx.musicbook.client.controller.musician;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseAwareController;
import ru.javafx.musicbook.client.controller.instrument.InstrumentPaneController;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianInstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/musician/InstrumentList.fxml")
@Scope("prototype")
public class InstrumentListController extends BaseAwareController {
    
    private Resource<Musician> resource;
    
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private MusicianRepository  musicianRepository;
    @Autowired
    private MusicianInstrumentRepository musicianInstrumentRepository;
       
    @FXML
    private AnchorPane instrumentList;
    @FXML
    private ListView<Resource<Instrument>> instrumentListView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        instrumentListView.setCellFactory((ListView<Resource<Instrument>> listView) -> new ListCell<Resource<Instrument>>() {
            @Override
            public void updateItem(Resource<Instrument> item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    this.setText(item.getContent().getName());
                }
            }
        });
        initRepositoryListeners();
    }

    public void bootstrap(Resource<Musician> resource) {
        this.resource = resource;
        setListValue();     
    }
    
    private void setListValue() {                
        List <Resource<Instrument>> instrumentResources = new ArrayList<>();
        instrumentListView.getItems().clear();
        try {
            instrumentResources.addAll(instrumentRepository.findByMusician(resource).getContent().parallelStream().collect(Collectors.toList()));
            if (!instrumentResources.isEmpty()) {
                instrumentListView.getItems().addAll(instrumentResources);
                sort();
            } else {
                Instrument emptyInstrument = new Instrument();
                emptyInstrument.setName("Unknown");
                Resource<Instrument> resorceInstrument = new Resource<>(emptyInstrument, new Link(Instrument.DEFAULT_INSTRUMENT));
                instrumentListView.getItems().add(resorceInstrument);
            }                     
            Helper.setHeightList(instrumentListView, 8); 
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }

    private void initRepositoryListeners() {   
        musicianInstrumentRepository.clearChangeListeners(this);                         
        musicianRepository.clearDeleteListeners(this);           
        musicianRepository.clearUpdateListeners(this);                
        instrumentRepository.clearDeleteListeners(this);           
        instrumentRepository.clearUpdateListeners(this);
        
        musicianInstrumentRepository.addChangeListener((observable, oldVal, newVal) -> setListValue(), this);                         
        musicianRepository.addDeleteListener((observable, oldVal, newVal) -> setListValue(), this);           
        musicianRepository.addUpdateListener((observable, oldVal, newVal) -> setListValue(), this);                
        instrumentRepository.addDeleteListener((observable, oldVal, newVal) -> setListValue(), this);           
        instrumentRepository.addUpdateListener((observable, oldVal, newVal) -> setListValue(), this);
    }
       
    private void sort() {
        instrumentListView.getSelectionModel().clearSelection();
        instrumentListView.getItems().sort(Comparator.comparing(res -> res.getContent().getName()));
    }  
    
    @FXML
    private void onMouseClickList(MouseEvent mouseEvent) {    
        contextMenuService.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {           
            Resource<Instrument> selectedItem = instrumentListView.getSelectionModel().getSelectedItem();
            // если лкм выбрана запись - показать её
            if (selectedItem != null && !selectedItem.getId().getHref().equals(Instrument.DEFAULT_INSTRUMENT)) {
                requestViewService.show(InstrumentPaneController.class, selectedItem);
            }            
        }
    }
    
}
