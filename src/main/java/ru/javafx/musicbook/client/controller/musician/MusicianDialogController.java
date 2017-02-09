
package ru.javafx.musicbook.client.controller.musician;

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
import javafx.scene.control.DatePicker;
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
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGenre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.MusicianGenreRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/musician/MusicianDialog.fxml",    
    title = "Musician Dialog Window")
@Scope("prototype")
public class MusicianDialogController extends BaseDialogController<Musician> {
    
    private Musician musician; 
    private final List<Genre> genres = new ArrayList<>();
    private final IntegerProperty rating = new SimpleIntegerProperty();

    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private MusicianGenreRepository musicianGenreRepository;
    
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    @FXML
    private ChoiceCheckBoxController<Genre> includedChoiceCheckBoxController;   
    @FXML
    private AnchorPane view;
    @FXML
    private TextField nameTextField;   
    @FXML
    private DatePicker dobDatePicker;
    @FXML
    private DatePicker dodDatePicker;
    @FXML
    private TextField countryTextField;
    @FXML
    private Spinner<Integer> ratingSpinner;
    @FXML
    private TextArea commentTextArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        Helper.initIntegerSpinner(ratingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);
        Helper.initDatePicker(dobDatePicker, true);
        Helper.initDatePicker(dodDatePicker, true);  
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(countryTextField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
        
        includedDialogImageBoxController.setStage(dialogStage);
        includedChoiceCheckBoxController.setMainPane(view);
        includedChoiceCheckBoxController.getChoiceCheckBox().setPrefWidth(250.0);
    }
    
    private void initGenreChoiceCheckBox() {
        Map<Resource<Genre>, ObservableValue<Boolean>> map = new HashMap<>();
        try {         
            if (edit) {
                genreRepository.findByMusician(resource).getContent().parallelStream().forEach(
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
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        String text = nameTextField.getText();
        if (text == null || text.trim().equals("")) {
            errorMessage += "Введите название музыканта!\n"; 
        } else {
            text = text.trim().toLowerCase();
            /*
            try {
                if (!album.getName().toLowerCase().equals(text) 
                        && artistRepository.getPagedResources("name=" + text + "&artist.id=" + Helper.getId(resource)).getMetadata().getTotalElements() > 0) {
                    errorMessage += "Такой альбом уже есть!\n";
                }
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
                //ex.printStackTrace();
            } 
            */
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
        musician = new Musician();
        initGenreChoiceCheckBox();      
    }
    
    @Override
    protected void edit() { 
        edit = true;
        musician = resource.getContent();
        oldResource = new Resource<>(musician.clone(), resource.getLinks());  
        
        nameTextField.setText(musician.getName());
        dobDatePicker.setValue(dobDatePicker.getConverter().fromString(musician.getDateOfBirth()));
        dodDatePicker.setValue(dodDatePicker.getConverter().fromString(musician.getDateOfDeath()));
        countryTextField.setText(musician.getCountry());
        ratingSpinner.getValueFactory().setValue(musician.getRating());
        commentTextArea.setText(musician.getDescription());
        initGenreChoiceCheckBox();
        if (resource.hasLink("get_image")) {
            includedDialogImageBoxController.setImage(resource.getLink("get_image").getHref()); 
        }
    }
    
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) { 
            musician.setName(nameTextField.getText().trim());
            musician.setCountry(countryTextField.getText() != null ? countryTextField.getText().trim() : "");
            musician.setDateOfBirth(dobDatePicker.getEditor().getText());
            musician.setDateOfDeath(dodDatePicker.getEditor().getText());
            musician.setRating(getRating());
            musician.setDescription(commentTextArea.getText() != null ? commentTextArea.getText().trim() : "");             
            try { 
                resource = edit ? musicianRepository.update(resource) : musicianRepository.saveAndGetResource(musician);
                logger.info("Saved Musician Resource: {}", resource);
                // Извлечь жанры из списка и сохранить их в связке
                includedChoiceCheckBoxController.getItemMap().keySet().parallelStream().forEach(resourceGenre -> {
                    ObservableValue<Boolean> flag = includedChoiceCheckBoxController.getItemMap().get(resourceGenre); 
                    try {
                        //удалить невыбранные жанры, если они есть у артиста
                        if (!flag.getValue() && genres.contains(resourceGenre.getContent())) {
                            Resource<MusicianGenre> artistGenreResource = musicianGenreRepository.findByMusicianAndGenre(resource, resourceGenre);                                                     
                            musicianGenreRepository.delete(artistGenreResource);
                        }
                        //добавить выбранные жанры, если их ещё нет
                        if (flag.getValue() && !genres.contains(resourceGenre.getContent())) { 
                            MusicianGenre musicianGenre = new MusicianGenre();
                            musicianGenre.setMusician(resource.getId().getHref());
                            musicianGenre.setGenre(resourceGenre.getId().getHref());
                            musicianGenreRepository.save(musicianGenre);
                        }
                    } catch (URISyntaxException ex) {
                        logger.error(ex.getMessage());
                    }   
                });            
                if (includedDialogImageBoxController.isChangedImage()) {
                    musicianRepository.saveImage(resource, includedDialogImageBoxController.getImage());
                    includedDialogImageBoxController.setChangedImage(false);                              
                }            
                if (edit) {
                    musicianRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    musicianRepository.setAdded(new WrapChangedEntity<>(null, resource));
                } 
                dialogStage.close();
                edit = false;
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }
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
