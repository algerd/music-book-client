
package ru.javafx.musicbook.client.controller.album;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.Params;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.controller.helper.choiceCheckBox.ChoiceCheckBoxController;
import ru.javafx.musicbook.client.controller.helper.inputImageBox.DialogImageBoxController;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/album/AlbumDialog.fxml",    
    title = "Album Dialog Window")
@Scope("prototype")
public class AlbumDialogController extends BaseDialogController<Album> {
    
    private Album album; 
    private final List<Genre> genres = new ArrayList<>();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final IntegerProperty minute = new SimpleIntegerProperty();
    private final IntegerProperty secund = new SimpleIntegerProperty();
    
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private GenreRepository genreRepository;
        
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    @FXML
    private ChoiceCheckBoxController<Genre> includedChoiceCheckBoxController;   
    @FXML
    private AnchorPane view;
    @FXML
    private ChoiceBox<Resource<Artist>> artistField;  
    @FXML
    private TextField nameField;
    @FXML
    private Spinner<Integer> yearField;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private Spinner<Integer> secundSpinner;
    @FXML
    private Spinner<Integer> ratingField;
    @FXML
    private TextArea commentTextArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        Helper.initIntegerSpinner(ratingField, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);
        Helper.initIntegerSpinner(yearField, Params.MIN_YEAR, Params.MAX_YEAR, Params.MIN_YEAR, true, year);
        Helper.initIntegerSpinner(minuteSpinner, 0, 100, 0, true, minute);
        Helper.initIntegerSpinner(secundSpinner, 0, 59, 0, true, secund);     
        Helper.limitTextInput(nameField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
        
        includedDialogImageBoxController.setStage(dialogStage);
        includedChoiceCheckBoxController.setMainPane(view);
        includedChoiceCheckBoxController.getChoiceCheckBox().setPrefWidth(250.0);
    }
    
    private void initGenreChoiceCheckBox() {
        Map<Resource<Genre>, ObservableValue<Boolean>> map = new HashMap<>();
        try {         
            if (edit) {
                /*
                genreRepository.findByAlbum(resource).getContent().parallelStream().forEach(
                    genreResource -> genres.add(genreResource.getContent())
                );
                */
            }   
            genreRepository.findAll().getContent().parallelStream().forEach(
                genre -> map.put(genre, new SimpleBooleanProperty(genres.contains(genre.getContent())))             
            );
            includedChoiceCheckBoxController.addItems(map);
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
            //ex.printStackTrace();
        }       
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        if (nameField.getText() == null || nameField.getText().trim().equals("")) {
            errorMessage += "Введите название альбома!\n"; 
        }
        try {
            if (!album.getName().equals(nameField.getText()) && artistRepository.existByName(nameField.getText())) {
                errorMessage += "Такой альбом уже есть!\n";
            }
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
            //ex.printStackTrace();
        }        
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
        if (resource == null) {
            album = new Album();
        } else {
            album = resource.getContent();
            oldResource = new Resource<>(album.clone(), resource.getLinks());
        }
        initGenreChoiceCheckBox();
    }
       
    @Override
    protected void edit() { 
        edit = true;
        album = resource.getContent();
        oldResource = new Resource<>(album.clone(), resource.getLinks());  
        
        nameField.setText(album.getName());
        yearField.getValueFactory().setValue(album.getYear());
        ratingField.getValueFactory().setValue(album.getRating());
        commentTextArea.setText(album.getDescription());     
        String timeString = album.getTime();       
        if (timeString.equals("")) {
            minuteSpinner.getValueFactory().setValue(0);
            secundSpinner.getValueFactory().setValue(0);
        }
        else {
            String[] timeArray = timeString.split(":");
            minuteSpinner.getValueFactory().setValue(Integer.parseInt(timeArray[0]));
            secundSpinner.getValueFactory().setValue(Integer.parseInt(timeArray[1]));
        } 
        initGenreChoiceCheckBox();    
        if (resource.hasLink("get_image")) {
            includedDialogImageBoxController.setImage(resource.getLink("get_image").getHref()); 
        }
    }
    
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) {
            //Artist artist = artistField.getValue();                         
            //album.setId_artist(artist.getId());
            album.setName(nameField.getText().trim());             
            album.setTime(getMinute() + ":" + ((getSecund() < 10) ? "0" : "") + getSecund());                     
            album.setDescription(commentTextArea.getText().trim());             
            album.setYear(getYear());
            album.setRating(getRating());  
            
        }
    }
    
    public int getYear() {
        return year.get();
    }
    public void setYear(int value) {
        year.set(value);
    }
    public IntegerProperty yearProperty() {
        return year;
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
    
    public int getMinute() {
        return minute.get();
    }
    public void setMinute(int value) {
        minute.set(value);
    }
    public IntegerProperty minuteProperty() {
        return minute;
    }
    
    public int getSecund() {
        return secund.get();
    }
    public void setSecund(int value) {
        secund.set(value);
    }
    public IntegerProperty secundProperty() {
        return secund;
    }

}
