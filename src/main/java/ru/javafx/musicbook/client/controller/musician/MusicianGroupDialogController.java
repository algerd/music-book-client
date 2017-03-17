package ru.javafx.musicbook.client.controller.musician;

import java.net.URISyntaxException;
import java.net.URL;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGroup;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.MusicianGroupRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.datacore.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/musician/MusicianGroupDialog.fxml",    
    title = "Musician Group Dialog Window")
@Scope("prototype")
public class MusicianGroupDialogController extends BaseDialogController<MusicianGroup> {
   
    private MusicianGroup musicianGroup;  
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private MusicianGroupRepository musicianGroupRepository;
       
    @FXML
    private AnchorPane view;
    @FXML
    private ChoiceBox<Resource<Musician>> musicianChoiceBox;
    @FXML
    private ChoiceBox<Resource<Artist>> artistChoiceBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Helper.initDatePicker(startDatePicker, true);
        Helper.initDatePicker(endDatePicker, true); 
        initMusicianChoiceBox();
        initArtistChoiceBox();
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
    
    private void initArtistChoiceBox() {
        artistChoiceBox.setConverter(new StringConverter<Resource<Artist>>() {
            @Override
            public String toString(Resource<Artist> res) {
                return res == null? null : res.getContent().getName();
            }
            @Override
            public Resource<Artist> fromString(String string) {
                return null;
            }
        });        
        List <Resource<Artist>> artistResources = new ArrayList<>();
        try {
            artistResources.addAll(artistRepository.findAllNames().getContent().parallelStream().collect(Collectors.toList()));         
            artistChoiceBox.getItems().addAll(artistResources);           
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
    }
    
    private void selectArtistChoiceBox(String path) {
        artistChoiceBox.getItems().forEach(res -> {             
            if (res.getId().getHref().equals(path)) {
                artistChoiceBox.getSelectionModel().select(res);
                return;
            }              
        });
    }
       
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) {
            musicianGroup.setStartDate(startDatePicker.getEditor().getText());
            musicianGroup.setEndDate(endDatePicker.getEditor().getText());
            musicianGroup.setArtist(artistChoiceBox.getValue().getId().getHref());
            musicianGroup.setMusician(musicianChoiceBox.getValue().getId().getHref());
            
            try { 
                if (edit) {
                    musicianGroupRepository.update(resource);
                    musicianGroupRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    musicianGroupRepository.save(musicianGroup);
                    musicianGroupRepository.setAdded(new WrapChangedEntity<>(null, resource));
                }  
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            } 
            
            dialogStage.close();
        }
    }
    
    @Override
    protected void add() {     
        musicianGroup = (resource == null) ? new MusicianGroup() : resource.getContent();      
        selectArtistChoiceBox(musicianGroup.getArtist());
        selectMusicianChoiceBox(musicianGroup.getMusician()); 
    }
    
    @Override
    protected void edit() {     
        edit = true;
        musicianGroup = resource.getContent();
        oldResource = new Resource<>(musicianGroup.clone(), resource.getLinks());       
        try {
            selectArtistChoiceBox(artistRepository.getResource(resource.getLink("artist").getHref()).getId().getHref());
            selectMusicianChoiceBox(musicianRepository.getResource(resource.getLink("musician").getHref()).getId().getHref());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
        startDatePicker.setValue(startDatePicker.getConverter().fromString(musicianGroup.getStartDate()));
        endDatePicker.setValue(endDatePicker.getConverter().fromString(musicianGroup.getEndDate()));            
    }
      
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        if (musicianChoiceBox.getValue().getId().getHref().equals(Musician.DEFAULT_MUSICIAN) 
                && artistChoiceBox.getValue().getId().getHref().equals(Artist.DEFAULT_ARTIST)) {
            errorMessage += "Выберите группу или музыканта из списка \n";
        }         
        try {           
            if (!edit && musicianGroupRepository.countByMusicianAndArtist(musicianChoiceBox.getValue(), artistChoiceBox.getValue()) > 0) {
                errorMessage += "Такой музыкант уже есть в группе \n";
            }
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }        
        try { 
            startDatePicker.getConverter().fromString(startDatePicker.getEditor().getText());
        } catch (DateTimeParseException e) {
            errorMessage += "Неправильно введён формат Start Date " + startDatePicker.getEditor().getText() +". Надо mm.dd.yyyy \n";
        }     
        try { 
            endDatePicker.getConverter().fromString(endDatePicker.getEditor().getText());
        } catch (DateTimeParseException e) {
            errorMessage += "Неправильно введён формат End Date " + endDatePicker.getEditor().getText() +". Надо mm.dd.yyyy \n";
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
