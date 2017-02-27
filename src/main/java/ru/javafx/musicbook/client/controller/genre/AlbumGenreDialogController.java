
package ru.javafx.musicbook.client.controller.genre;

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
import ru.javafx.musicbook.client.entity.AlbumGenre;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumGenreRepository;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;

@FXMLController(
    value = "/fxml/genre/AlbumGenreDialog.fxml",    
    title = "Album Genre Dialog Window")
@Scope("prototype")
public class AlbumGenreDialogController extends BaseDialogController<AlbumGenre> {
    
    private AlbumGenre albumGenre;  
    
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;    
    @Autowired
    private AlbumGenreRepository albumGenreRepository;
    
    @FXML
    private ChoiceBox<Resource<Genre>> genreChoiceBox;
    @FXML
    private ChoiceBox<Resource<Artist>> artistChoiceBox;
    @FXML
    private ChoiceBox<Resource<Album>> albumChoiceBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initGenreChoiceBox();
        initArtistChoiceBox();
        initAlbumChoiceBox();
        // При выборе другого артиста - обновить список альбомов в ChoiceBox
        artistChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::changeAlbumChoiceCheckBox);    
    }
    
    private void initGenreChoiceBox() {
        genreChoiceBox.setConverter(new StringConverter<Resource<Genre>>() {
            @Override
            public String toString(Resource<Genre> res) {
                return res == null? null : res.getContent().getName();
            }
            @Override
            public Resource<Genre> fromString(String string) {
                return null;
            }
        });      
        List <Resource<Genre>> genreResources = new ArrayList<>();
        try {
            genreResources.addAll(genreRepository.findAllNames().getContent().parallelStream().collect(Collectors.toList()));         
            genreChoiceBox.getItems().addAll(genreResources);          
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    private void selectGenreChoiceBox(String path) {
        genreChoiceBox.getItems().forEach(res -> {             
            if (res.getId().getHref().equals(path)) {
                genreChoiceBox.getSelectionModel().select(res);
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
            albumGenre.setAlbum(albumChoiceBox.getValue().getId().getHref());
            albumGenre.setGenre(genreChoiceBox.getValue().getId().getHref());           
            try { 
                if (edit) {
                    albumGenreRepository.update(resource);
                    albumGenreRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    albumGenreRepository.save(albumGenre);
                    albumGenreRepository.setAdded(new WrapChangedEntity<>(null, resource));
                }  
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }        
            dialogStage.close();
        }
    }
    
    @Override
    protected void add() {     
        albumGenre = (resource == null) ? new AlbumGenre() : resource.getContent();      
        try {
            Resource<Album> resAlbum = albumRepository.getResource(albumGenre.getAlbum());
            selectArtistChoiceBox(artistRepository.getResource(resAlbum.getLink("artist").getHref()).getId().getHref());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }           
        selectAlbumChoiceBox(albumGenre.getAlbum());
        selectGenreChoiceBox(albumGenre.getGenre()); 
    }
    
    @Override
    protected void edit() {     
        edit = true;
        albumGenre = resource.getContent();
        oldResource = new Resource<>(albumGenre.clone(), resource.getLinks());       
        try {
            Resource<Album> resAlbum = albumRepository.getResource(resource.getLink("album").getHref());
            selectArtistChoiceBox(artistRepository.getResource(resAlbum.getLink("artist").getHref()).getId().getHref());          
            selectAlbumChoiceBox(resAlbum.getId().getHref());
            selectGenreChoiceBox(genreRepository.getResource(resource.getLink("genre").getHref()).getId().getHref());
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
            else if (genreChoiceBox.getValue().getId().getHref().equals(Genre.DEFAULT_GENRE) 
                    && albumChoiceBox.getValue().getId().getHref().equals(Album.DEFAULT_ALBUM)) {
                errorMessage += "Выберите альбом и жанр из списка \n";
            } 
            else if (!edit && albumGenreRepository.countByAlbumAndGenre(albumChoiceBox.getValue(), genreChoiceBox.getValue()) > 0) {
                errorMessage += "Такой жанр уже есть в альбоме \n";
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
