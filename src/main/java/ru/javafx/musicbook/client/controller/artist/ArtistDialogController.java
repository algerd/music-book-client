
package ru.javafx.musicbook.client.controller.artist;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import ru.javafx.musicbook.client.Params;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/artist/ArtistDialog.fxml",    
    title = "Artist Dialog Window")
@Scope("prototype")
public class ArtistDialogController extends BaseDialogController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Artist artist; 
    private final IntegerProperty rating = new SimpleIntegerProperty();
    
    @Autowired
    private ArtistRepository artistRepository;
    
    @FXML
    private TextField nameTextField;
    @FXML
    private Spinner<Integer> ratingSpinner;    
    @FXML
    private TextArea commentTextArea;
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        Helper.initIntegerSpinner(ratingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);      
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
    }
         
    @FXML
    private void handleOkButton() {
        if (isInputValid()) { 
            artist.setName(nameTextField.getText().trim());
            artist.setRating(getRating());
            artist.setDescription(commentTextArea.getText().trim());           

            if (edit) {               
                //logger.info("Edited Artist: {}", artist);
                artistRepository.update(resource);
            } else { 
                //logger.info("Added Artist: {}", artist);
                artistRepository.add(artist);
            }             
            dialogStage.close();
            edit = false;
        }
    }
    
    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
           
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        
        if (nameTextField.getText() == null || nameTextField.getText().trim().equals("")) {
            errorMessage += "Введите имя артиста!\n"; 
        } 
        /*
        if (!edit && !repositoryService.getArtistRepository().isUniqueColumnValue("name", nameTextField.getText())) {
            errorMessage += "Такой артист уже есть!\n";
        } 
        */
        if (errorMessage.equals("")) {
            return true;
        } 
        else {
            errorMessage(errorMessage);         
            return false;
        }
    }
      
    @Override
    protected void add() {
        artist = new Artist();
    }
       
    @Override
    protected void edit() { 
        edit = true;
        artist = (Artist) resource.getContent();
        
        nameTextField.setText(artist.getName());
        ratingSpinner.getValueFactory().setValue(artist.getRating());
        commentTextArea.setText(artist.getDescription());
    }
        
    public int getRating() {
        return rating.get();
    }
    public void setRating(int value) {
        rating.set(value);
    }
    public IntegerProperty ratingProperty() {
        return rating;
    }
              
}
