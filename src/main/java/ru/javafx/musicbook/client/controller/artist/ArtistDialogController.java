
package ru.javafx.musicbook.client.controller.artist;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.Params;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.controller.helper.choiceCheckBox.ChoiceCheckBoxController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
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
    @Autowired
    private GenreRepository genreRepository;   
    @FXML
    private ChoiceCheckBoxController<Genre> includedChoiceCheckBoxController;
    
    @FXML
    private AnchorPane view;
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
        includedChoiceCheckBoxController.setMainPane(view);
        includedChoiceCheckBoxController.getChoiceCheckBox().setPrefWidth(250.0);
    }
    
    private void initGenreChoiceCheckBox() {
        /*
        List<GenreEntity> artistGenres = new ArrayList<>();
        if (edit) {        
            repositoryService.getArtistGenreRepository().selectArtistGenreByArtist(artist).stream().forEach(artistGenre -> {
                artistGenres.add(artistGenre.getGenre());
            });
        }
        Map<GenreEntity, ObservableValue<Boolean>> map = new HashMap<>();
        repositoryService.getGenreRepository().selectAll().stream().forEach(genre -> {                     
            map.put(genre, new SimpleBooleanProperty(artistGenres.contains(genre)));
        });
        includedChoiceCheckBoxController.addItems(map);
        */
        Map<Resource<Genre>, ObservableValue<Boolean>> map = new HashMap<>();
        try {
            genreRepository.getAll().getContent().parallelStream().forEach(
                genre -> map.put(genre, new SimpleBooleanProperty(true))    
            );
            includedChoiceCheckBoxController.addItems(map);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        
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
        initGenreChoiceCheckBox();      
    }
       
    @Override
    protected void edit() { 
        edit = true;
        artist = (Artist) resource.getContent();       
        nameTextField.setText(artist.getName());
        ratingSpinner.getValueFactory().setValue(artist.getRating());
        commentTextArea.setText(artist.getDescription());
        initGenreChoiceCheckBox();
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
