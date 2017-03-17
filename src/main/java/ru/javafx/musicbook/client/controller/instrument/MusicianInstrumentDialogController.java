
package ru.javafx.musicbook.client.controller.instrument;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianInstrument;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianInstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.datacore.impl.WrapChangedEntity;

@FXMLController(
    value = "/fxml/instrument/MusicianInstrumentDialog.fxml",    
    title = "Musician Instrument Dialog Window")
@Scope("prototype")
public class MusicianInstrumentDialogController extends BaseDialogController<MusicianInstrument> {
    
    private MusicianInstrument musicianInstrument;
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private MusicianInstrumentRepository musicianInstrumentRepository;
    
    @FXML
    private ChoiceBox<Resource<Instrument>> instrumentChoiceBox;
    @FXML
    private ChoiceBox<Resource<Musician>> musicianChoiceBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initInstrumentChoiceBox();
        initMusicianChoiceBox();
    }
    
    private void initInstrumentChoiceBox() {
        instrumentChoiceBox.setConverter(new StringConverter<Resource<Instrument>>() {
            @Override
            public String toString(Resource<Instrument> res) {
                return res == null? null : res.getContent().getName();
            }
            @Override
            public Resource<Instrument> fromString(String string) {
                return null;
            }
        });      
        List <Resource<Instrument>> instrumentResources = new ArrayList<>();
        try {
            instrumentResources.addAll(instrumentRepository.findAllNames().getContent().parallelStream().collect(Collectors.toList()));         
            instrumentChoiceBox.getItems().addAll(instrumentResources);          
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    private void selectInstrumentChoiceBox(String path) {
        instrumentChoiceBox.getItems().forEach(res -> {             
            if (res.getId().getHref().equals(path)) {
                instrumentChoiceBox.getSelectionModel().select(res);
                return;
            }              
        });
    }
    
    private void initMusicianChoiceBox() {
        musicianChoiceBox.setConverter(new StringConverter<Resource<Musician>>() {
            @Override
            public String toString(Resource<Musician> res) {
                return res == null? null : res.getContent().getName();
            }
            @Override
            public Resource<Musician> fromString(String string) {
                return null;
            }
        });      
        List <Resource<Musician>> musicianResources = new ArrayList<>();
        try {
            musicianResources.addAll(musicianRepository.findAllNames().getContent().parallelStream().collect(Collectors.toList()));         
            musicianChoiceBox.getItems().addAll(musicianResources);          
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    private void selectMusicianChoiceBox(String path) {
        musicianChoiceBox.getItems().forEach(res -> {             
            if (res.getId().getHref().equals(path)) {
                musicianChoiceBox.getSelectionModel().select(res);
                return;
            }              
        });
    }
    
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) {
            musicianInstrument.setMusician(musicianChoiceBox.getValue().getId().getHref());
            musicianInstrument.setInstrument(instrumentChoiceBox.getValue().getId().getHref());           
            try { 
                if (edit) {
                    musicianInstrumentRepository.update(resource);
                    musicianInstrumentRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    musicianInstrumentRepository.save(musicianInstrument);
                    musicianInstrumentRepository.setAdded(new WrapChangedEntity<>(null, resource));
                }  
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }        
            dialogStage.close();
        }
    }
    
    @Override
    protected void add() {     
        musicianInstrument = (resource == null) ? new MusicianInstrument() : resource.getContent();                
        selectMusicianChoiceBox(musicianInstrument.getMusician());
        selectInstrumentChoiceBox(musicianInstrument.getInstrument()); 
    }
    
    @Override
    protected void edit() {     
        edit = true;
        musicianInstrument = resource.getContent();
        oldResource = new Resource<>(musicianInstrument.clone(), resource.getLinks());       
        try {
            selectInstrumentChoiceBox(instrumentRepository.getResource(resource.getLink("instrument").getHref()).getId().getHref());          
            selectMusicianChoiceBox(musicianRepository.getResource(resource.getLink("musician").getHref()).getId().getHref());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }              
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";                        
        try {                  
            if (musicianChoiceBox.getValue().getId().getHref().equals(Artist.DEFAULT_ARTIST) 
                    && instrumentChoiceBox.getValue().getId().getHref().equals(Instrument.DEFAULT_INSTRUMENT)) {
                errorMessage += "Выберите музыканта или инструмент из списка \n";
            } 
            else if (musicianInstrumentRepository.countByMusicianAndInstrument(musicianChoiceBox.getValue(), instrumentChoiceBox.getValue()) > 0) {
                errorMessage += "Такой инструмент уже есть у музыканта \n";
            }
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        } 
        
        if (errorMessage.equals("")) {
            return true;
        } 
        else {
            errorMessage(errorMessage);          
            return false;
        }   
    }   
}
