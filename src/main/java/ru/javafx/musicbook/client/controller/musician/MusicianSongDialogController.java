
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
import ru.javafx.musicbook.client.entity.MusicianSong;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.MusicianSongRepository;
import ru.javafx.musicbook.client.repository.SongRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;

@FXMLController(
    value = "/fxml/musician/MusicianSongDialog.fxml",    
    title = "Musician Song Dialog Window")
@Scope("prototype")
public class MusicianSongDialogController extends BaseDialogController<MusicianSong> {
    
    private MusicianSong musicianSong;
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository; 
    @Autowired
    private SongRepository songRepository;     
    @Autowired
    private MusicianSongRepository musicianSongRepository;
    
    @FXML
    private ChoiceBox<Resource<Musician>> musicianChoiceBox;
    @FXML
    private ChoiceBox<Resource<Artist>> artistChoiceBox;
    @FXML
    private ChoiceBox<Resource<Album>> albumChoiceBox;
    @FXML
    private ChoiceBox<Resource<Song>> songChoiceBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMusicianChoiceBox();
        initArtistChoiceBox();
        initAlbumChoiceBox();
        initSongChoiceBox();
        // При выборе другого артиста - обновить список альбомов в ChoiceBox
        artistChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::changeAlbumChoiceCheckBox);   
        // При выборе другого альбома - обновить список песен в ChoiceBox
        albumChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::changeSongChoiceCheckBox);   
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
    
    private void initSongChoiceBox() {       
        songChoiceBox.setConverter(new StringConverter<Resource<Song>>() {
            @Override
            public String toString(Resource<Song> res) {
                return res == null? null : res.getContent().getName();
            }
            @Override
            public Resource<Song> fromString(String string) {
                return null;
            }
        });              
    }
    
    private void selectSongChoiceBox(String path) {
        songChoiceBox.getItems().forEach(res -> {             
            if (res.getId().getHref().equals(path)) {
                songChoiceBox.getSelectionModel().select(res);
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
    
    private void changeSongChoiceCheckBox(ObservableValue<? extends Object> observable, Resource<Album> oldValue, Resource<Album> newValue) {
        songChoiceBox.getItems().clear();
        if (!albumChoiceBox.getItems().isEmpty()) {
            try { 
                Resource<Album> resAlbum = albumChoiceBox.getSelectionModel().getSelectedItem();            
                songChoiceBox.getItems().addAll(songRepository.findByAlbum(resAlbum).getContent().parallelStream().collect(Collectors.toList()));        
                songChoiceBox.getSelectionModel().selectFirst();
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            } 
        }
    }
    
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) {
            musicianSong.setSong(songChoiceBox.getValue().getId().getHref());
            musicianSong.setMusician(musicianChoiceBox.getValue().getId().getHref());           
            try { 
                if (edit) {
                    musicianSongRepository.update(resource);
                    musicianSongRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    musicianSongRepository.save(musicianSong);
                    musicianSongRepository.setAdded(new WrapChangedEntity<>(null, resource));
                }  
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }           
            dialogStage.close();
        }
    }
    
    @Override
    protected void add() {     
        musicianSong = (resource == null) ? new MusicianSong() : resource.getContent();   
        if (musicianSong.getSong() != null) selectSongChoiceBox(musicianSong.getSong());
        selectArtistChoiceBox(Artist.DEFAULT_ARTIST);
        selectAlbumChoiceBox(Album.DEFAULT_ALBUM);
        selectMusicianChoiceBox(musicianSong.getMusician()); 
    }
    
    @Override
    protected void edit() {     
        edit = true;
        musicianSong = resource.getContent();
        oldResource = new Resource<>(musicianSong.clone(), resource.getLinks());       
        try {
            Resource<Song> resSong = songRepository.getResource(resource.getLink("song").getHref());
            Resource<Album> resAlbum = albumRepository.getResource(resSong.getLink("album").getHref());
            Resource<Artist> resArtist = artistRepository.getResource(resAlbum.getLink("artist").getHref());
            selectArtistChoiceBox(resArtist.getId().getHref());          
            selectAlbumChoiceBox(resAlbum.getId().getHref());
            selectSongChoiceBox(resSong.getId().getHref());
            selectMusicianChoiceBox(musicianRepository.getResource(resource.getLink("musician").getHref()).getId().getHref());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }              
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";                        
        try {             
            if (songChoiceBox.getValue() == null) {
                errorMessage += "Выберите песню из списка \n";
            }
            else if (!edit && musicianSongRepository.countByMusicianAndSong(musicianChoiceBox.getValue(), songChoiceBox.getValue()) > 0) {
                errorMessage += "Такой музыкант уже есть в песне \n";
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
