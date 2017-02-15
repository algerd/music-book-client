package ru.javafx.musicbook.client.controller.musician;

import java.net.URL;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGroup;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.MusicianGroupRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/musician/MusicianGroupDialog.fxml",    
    title = "Musician Group Dialog Window")
@Scope("prototype")
public class MusicianGroupDialogController extends BaseDialogController<MusicianGroup> {
   
    private MusicianGroup musicianGroup;  
    private final List<Artist> artists = new ArrayList<>();
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private MusicianGroupRepository musicianGroupRepository;
       
    @FXML
    private AnchorPane view;
    @FXML
    private ChoiceBox<Musician> musicianChoiceBox;
    @FXML
    private ChoiceBox<Artist> artistChoiceBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Helper.initDatePicker(startDatePicker, true);
        Helper.initDatePicker(endDatePicker, true);       
    }  
    
    @FXML
    protected void handleOkButton() {
        if (isInputValid()) {
           
            dialogStage.close();
        }
    }
    
    @Override
    protected void edit() {     
       
        //startDatePicker.setValue(startDatePicker.getConverter().fromString(musicianGroup.getStart_date()));
        //endDatePicker.setValue(endDatePicker.getConverter().fromString(musicianGroup.getEnd_date()));            
    }
    
    @Override
    protected void add() {     
          
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        
        if (musicianChoiceBox.getValue() == null) {
            errorMessage += "Выберите музыканта из списка \n";
        }       
        if (artistChoiceBox.getValue() == null) {
            errorMessage += "Выберите группу из списка \n";
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
