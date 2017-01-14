
package ru.javafx.musicbook.client.controller.artist;

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
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/artist/ArtistDialog.fxml",    
    title = "Artist Dialog Window")
@Scope("prototype")
public class ArtistDialogController extends BaseDialogController<Artist> {
      
    private Artist artist; 
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final List<Genre> genres = new ArrayList<>();
    
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;  
    
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
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
        includedDialogImageBoxController.setStage(dialogStage);
        includedChoiceCheckBoxController.setMainPane(view);
        includedChoiceCheckBoxController.getChoiceCheckBox().setPrefWidth(250.0);
    }
    
    private void initGenreChoiceCheckBox() {
        Map<Resource<Genre>, ObservableValue<Boolean>> map = new HashMap<>();
        try {         
            if (edit) {
                genreRepository.findByArtist(resource).getContent().parallelStream().forEach(
                    genreResource -> genres.add(genreResource.getContent())
                );                     
            }   
            genreRepository.findAllNames().getContent().parallelStream().forEach(
                genre -> map.put(genre, new SimpleBooleanProperty(genres.contains(genre.getContent())))             
            );
            includedChoiceCheckBoxController.addItems(map);
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }       
    }
         
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) { 
            artist.setName(nameTextField.getText().trim());
            artist.setRating(getRating());
            artist.setDescription(commentTextArea.getText().trim());  
            
            try { 
                if (edit) {                             
                    artistRepository.update(resource);               
                } else {
                    resource = artistRepository.saveAndGetResource(artist); 
                }       
                // Извлечь жанры из списка и сохранить их в связке связанные с артистом
                includedChoiceCheckBoxController.getItemMap().keySet().parallelStream().forEach(resourceGenre -> {
                    ObservableValue<Boolean> flag = includedChoiceCheckBoxController.getItemMap().get(resourceGenre); 
                    String href = resourceGenre.getId().getHref();
                    int idGenre = Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));
                    //удалить невыбранные жанры, если они есть у артиста
                    try {
                        if (!flag.getValue() && genres.contains(resourceGenre.getContent())) {
                                artistRepository.deleteGenreFromArtist(resource, idGenre);
                        }
                        //добавить выбранные жанры, если их ещё нет
                        if (flag.getValue() && !genres.contains(resourceGenre.getContent())) { 
                            artistRepository.saveGenreInArtist(resource, idGenre);
                        }
                    } catch (URISyntaxException ex) {
                        logger.error(ex.getMessage());
                        //ex.printStackTrace();
                    }                   
                });            
                if (includedDialogImageBoxController.isChangedImage()) {
                    artistRepository.saveImage(resource, includedDialogImageBoxController.getImage());
                    includedDialogImageBoxController.setChangedImage(false);                              
                }            
                if (edit) {
                    artistRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    artistRepository.setAdded(new WrapChangedEntity<>(null, resource));
                } 
                dialogStage.close();
                edit = false;
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
                //ex.printStackTrace();
            }
        }
    }
       
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        
        if (nameTextField.getText() == null || nameTextField.getText().trim().equals("")) {
            errorMessage += "Введите имя артиста!\n"; 
        }     
        try {
            if (!artist.getName().equals(nameTextField.getText()) && artistRepository.existByName(nameTextField.getText())) {
                errorMessage += "Такой артист уже есть!\n";
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
        artist = new Artist();
        initGenreChoiceCheckBox();      
    }
       
    @Override
    protected void edit() { 
        edit = true;
        artist = resource.getContent();
        oldResource = new Resource<>(artist.clone(), resource.getLinks());  
        
        nameTextField.setText(artist.getName());
        ratingSpinner.getValueFactory().setValue(artist.getRating());
        commentTextArea.setText(artist.getDescription());
        initGenreChoiceCheckBox();
        if (resource.hasLink("get_image")) {
            includedDialogImageBoxController.setImage(resource.getLink("get_image").getHref()); 
        }
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
