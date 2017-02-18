
package ru.javafx.musicbook.client.controller.musician;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianAlbum;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.MusicianAlbumRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;

@FXMLController(
    value = "/fxml/musician/MusicianAlbumDialog.fxml",    
    title = "Musician Album Dialog Window")
@Scope("prototype")
public class MusicianAlbumDialogController extends BaseDialogController<MusicianAlbum> {
    
    private MusicianAlbum musicianAlbum;  
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;    
    @Autowired
    private MusicianAlbumRepository musicianAlbumRepository;
    
    @FXML
    private ChoiceBox<Resource<Musician>> musicianChoiceBox;
    @FXML
    private ChoiceBox<Resource<Artist>> artistChoiceBox;
    @FXML
    private ChoiceBox<Resource<Album>> albumChoiceBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMusicianChoiceBox();
        initArtistChoiceBox();
        initAlbumChoiceBox();
        // При выборе другого артиста - обновить список альбомов в ChoiceBox
        artistChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::changeAlbumChoiceCheckBox);    
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
    
    private void initAlbumChoiceBox() {       
        albumChoiceBox.setConverter(new StringConverter<Resource<Album>>() {
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
    
    private void selectAlbumChoiceBox(String path) {
        albumChoiceBox.getItems().forEach(res -> {             
            if (res.getId().getHref().equals(path)) {
                albumChoiceBox.getSelectionModel().select(res);
                return;
            }              
        });
    }

    private void changeAlbumChoiceCheckBox(ObservableValue<? extends Object> observable, Resource<Artist> oldValue, Resource<Artist> newValue) {
        try { 
            Resource<Artist> resArtist = artistChoiceBox.getSelectionModel().getSelectedItem();
            albumChoiceBox.getItems().clear();
            albumChoiceBox.getItems().addAll(albumRepository.findByArtist(resArtist).getContent().parallelStream().collect(Collectors.toList()));        
            albumChoiceBox.getSelectionModel().selectFirst();
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
    }
   
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) {
            musicianAlbum.setAlbum(albumChoiceBox.getValue().getId().getHref());
            musicianAlbum.setMusician(musicianChoiceBox.getValue().getId().getHref());           
            try { 
                if (edit) {
                    musicianAlbumRepository.update(resource);
                    musicianAlbumRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    musicianAlbumRepository.save(musicianAlbum);
                    musicianAlbumRepository.setAdded(new WrapChangedEntity<>(null, resource));
                }  
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            } 
            
            dialogStage.close();
        }
    }
    
    @Override
    protected void add() {     
        musicianAlbum = (resource == null) ? new MusicianAlbum() : resource.getContent();      
        try {
            Resource<Album> resAlbum = albumRepository.getResource(musicianAlbum.getAlbum());
            selectArtistChoiceBox(artistRepository.getResource(resAlbum.getLink("artist").getHref()).getId().getHref());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }           
        selectAlbumChoiceBox(musicianAlbum.getAlbum());
        selectMusicianChoiceBox(musicianAlbum.getMusician()); 
    }
    
    @Override
    protected void edit() {     
        edit = true;
        musicianAlbum = resource.getContent();
        oldResource = new Resource<>(musicianAlbum.clone(), resource.getLinks());       
        try {
            Resource<Album> resAlbum = albumRepository.getResource(resource.getLink("album").getHref());
            selectArtistChoiceBox(artistRepository.getResource(resAlbum.getLink("artist").getHref()).getId().getHref());          
            selectAlbumChoiceBox(resAlbum.getId().getHref());
            selectMusicianChoiceBox(musicianRepository.getResource(resource.getLink("musician").getHref()).getId().getHref());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }              
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";                        
        try {             
            if (albumChoiceBox.getValue() == null) {
                errorMessage += "Выберите группу c альбомом из списка \n";
            }      
            else if (musicianChoiceBox.getValue().getId().getHref().equals(Musician.DEFAULT_MUSICIAN) 
                    && albumChoiceBox.getValue().getId().getHref().equals(Album.DEFAULT_ALBUM)
                    && artistChoiceBox.getValue().getId().getHref().equals(Artist.DEFAULT_ARTIST)) {
                errorMessage += "Выберите группу, альбом или музыканта из списка \n";
            } 
            else if (!edit && musicianAlbumRepository.countByMusicianAndAlbum(musicianChoiceBox.getValue(), albumChoiceBox.getValue()) > 0) {
                errorMessage += "Такой музыкант уже есть в альбоме \n";
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
