
package ru.javafx.musicbook.client.controller.song;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.util.StringConverter;
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
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.entity.SongGenre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.SongGenreRepository;
import ru.javafx.musicbook.client.repository.SongRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/song/SongDialog.fxml",    
    title = "Song Dialog Window")
@Scope("prototype")
public class SongDialogController extends BaseDialogController<Song> {
    
    private Song song; 
    private final List<Genre> genres = new ArrayList<>();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final IntegerProperty track = new SimpleIntegerProperty();
    private final IntegerProperty minute = new SimpleIntegerProperty();
    private final IntegerProperty secund = new SimpleIntegerProperty();
    
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private SongGenreRepository songGenreRepository;
    
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    @FXML
    private ChoiceCheckBoxController<Genre> includedChoiceCheckBoxController;   
    @FXML
    private AnchorPane view;
    @FXML
    private ChoiceBox<Resource<Artist>> artistField;
    @FXML
    private ChoiceBox<Resource<Album>> albumField;
    @FXML
    private TextField nameField;
    @FXML
    private Spinner<Integer> trackField;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private Spinner<Integer> secundSpinner;
    @FXML
    private Spinner<Integer> ratingField;
    @FXML
    private TextArea lyricField;
    @FXML
    private TextArea commentTextArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        Helper.initIntegerSpinner(ratingField, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);
        Helper.initIntegerSpinner(trackField, Params.MIN_TRACK, Params.MAX_TRACK, Params.MIN_TRACK, true, track);
        Helper.initIntegerSpinner(minuteSpinner, 0, 100, 0, true, minute);
        Helper.initIntegerSpinner(secundSpinner, 0, 59, 0, true, secund);
        Helper.limitTextInput(nameField, 255);
        Helper.limitTextInput(lyricField, 10000); //???
        Helper.limitTextInput(commentTextArea, 10000);
              
        initArtistChoiceCheckBox();
        initAlbumChoiceCheckBox();
        includedDialogImageBoxController.setStage(dialogStage);
        includedChoiceCheckBoxController.setMainPane(view);
        includedChoiceCheckBoxController.getChoiceCheckBox().setPrefWidth(250.0);

        // При выборе другого артиста - обновить список альбомов в ChoiceBox
        artistField.getSelectionModel().selectedItemProperty().addListener(this::changeAlbumChoiceCheckBox);    
    }
    
    private void initArtistChoiceCheckBox() {       
        artistField.setConverter(new StringConverter<Resource<Artist>>() {
            @Override
            public String toString(Resource<Artist> res) {
                return res == null? null : res.getContent().getName();
            }
            @Override
            public Resource<Artist> fromString(String string) {
                return null;
            }
        });
        try { 
            artistField.getItems().addAll(artistRepository.findAllNames().getContent().parallelStream().collect(Collectors.toList()));        
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    private void initAlbumChoiceCheckBox() {       
        albumField.setConverter(new StringConverter<Resource<Album>>() {
            @Override
            public String toString(Resource<Album> res) {
                return res == null? null : res.getContent().getName();
            }
            @Override
            public Resource<Album> fromString(String string) {
                return null;
            }
        });              
    }
    
    /**
     * При выборе другого артиста - обновить список альбомов в ChoiceBox albumField.
     */
    private void changeAlbumChoiceCheckBox(ObservableValue<? extends Object> observable, Resource<Artist> oldValue, Resource<Artist> newValue) {
        try { 
            Resource<Artist> resArtist = artistField.getSelectionModel().getSelectedItem();
            albumField.getItems().clear();
            albumField.getItems().addAll(albumRepository.findByArtist(resArtist).getContent().parallelStream().collect(Collectors.toList()));        
            albumField.getSelectionModel().selectFirst();
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
    }
    
    private void initGenreChoiceCheckBox() {
        Map<Resource<Genre>, ObservableValue<Boolean>> map = new HashMap<>();
        try {         
            if (edit) {
                genreRepository.findBySong(resource).getContent().parallelStream().forEach(
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
        String text = nameField.getText();
        if (text == null || text.trim().equals("")) {
            errorMessage += "Введите название песни!\n"; 
        } else {
            text = text.trim().toLowerCase();
            /*
            try {
                if (!album.getName().toLowerCase().equals(text) 
                        && artistRepository.getPagedResources("name=" + text + "&artist.id=" + Helper.getId(resource)).getMetadata().getTotalElements() > 0) {
                    errorMessage += "Такая песня уже есть у альбома!\n";
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
        try {
            Resource<Artist> artistResource;
            Resource<Album> albumResource;
            if (resource == null) {
                song = new Song();
                albumResource = albumRepository.getResource(song.getAlbum());
            } else {
                song = resource.getContent();
                oldResource = new Resource<>(song.clone(), resource.getLinks()); 
                albumResource = albumRepository.getResource(resource.getLink("album").getHref());
            }
            artistResource = artistRepository.getResource(albumResource.getLink("artist").getHref()); 
            selectArtist(artistResource);
            selectAlbum(albumResource);
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
        initGenreChoiceCheckBox();
    }
    
    private void selectAlbum(Resource<Album> albumResource) {
        albumField.getItems().forEach(res -> {             
            if (res.getContent().getName().equals(albumResource.getContent().getName())) {
                albumField.getSelectionModel().select(res);
                return;
            }              
        });
    }
    
    private void selectArtist(Resource<Artist> artistResource) {
        artistField.getItems().forEach(res -> {             
            if (res.getContent().getName().equals(artistResource.getContent().getName())) {
                artistField.getSelectionModel().select(res);
                return;
            }              
        });
    }
    
    @Override
    protected void edit() { 
        edit = true;
        song = resource.getContent();
        oldResource = new Resource<>(song.clone(), resource.getLinks());      
        try {
            Resource<Album> albumResource = albumRepository.getResource(resource.getLink("album").getHref());    
            Resource<Artist> artistResource = artistRepository.getResource(albumResource.getLink("artist").getHref());                        
            selectArtist(artistResource);
            selectAlbum(albumResource);
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }        
        nameField.setText(song.getName());
        ratingField.getValueFactory().setValue(song.getRating());
        lyricField.setText(song.getLyric());
        commentTextArea.setText(song.getDescription());     
        String timeString = song.getTime();       
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
            song.setName(nameField.getText().trim());             
            song.setTime(getMinute() + ":" + ((getSecund() < 10) ? "0" : "") + getSecund());                     
            song.setDescription(commentTextArea.getText() != null ? commentTextArea.getText().trim() : "");  
            song.setLyric(lyricField.getText() != null ? lyricField.getText().trim() : "");
            song.setRating(getRating());   
            song.setAlbum(albumField.getValue().getId().getHref());
            try {
                resource = edit ? songRepository.update(resource) : songRepository.saveAndGetResource(song);
                logger.info("Saved Song Resource: {}", resource);
                // Извлечь жанры из списка и сохранить их в связке связанные с артистом
                includedChoiceCheckBoxController.getItemMap().keySet().parallelStream().forEach(resourceGenre -> {
                    ObservableValue<Boolean> flag = includedChoiceCheckBoxController.getItemMap().get(resourceGenre);                                       
                    try {
                        //удалить невыбранные жанры, если они есть у песни
                        if (!flag.getValue() && genres.contains(resourceGenre.getContent())) {
                            Resource<SongGenre> songGenreResource = songGenreRepository.findBySongAndGenre(resource, resourceGenre);                                                     
                            songGenreRepository.delete(songGenreResource);
                        }
                        //добавить выбранные жанры, если их ещё нет
                        if (flag.getValue() && !genres.contains(resourceGenre.getContent())) { 
                            SongGenre songGenre = new SongGenre();
                            songGenre.setSong(resource.getId().getHref());
                            songGenre.setGenre(resourceGenre.getId().getHref());
                            songGenreRepository.save(songGenre);
                        }
                    } catch (URISyntaxException ex) {
                        logger.error(ex.getMessage());
                    }                     
                });               
                if (includedDialogImageBoxController.isChangedImage()) {
                    songRepository.saveImage(resource, includedDialogImageBoxController.getImage());
                    includedDialogImageBoxController.setChangedImage(false);                              
                }               
                if (edit) {
                    songRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    songRepository.setAdded(new WrapChangedEntity<>(null, resource));
                }                
                dialogStage.close();
                edit = false;
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }
            
        }
    }
       
    public int getTrack() {
        return track.get();
    }
    public void setTrack(int value) {
        track.set(value);
    }
    public IntegerProperty trackProperty() {
        return track;
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
