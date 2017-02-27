
package ru.javafx.musicbook.client.controller.genre;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.ArtistGenre;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistGenreRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;

@FXMLController(
    value = "/fxml/genre/ArtistGenreDialog.fxml",    
    title = "Artist Genre Dialog Window")
@Scope("prototype")
public class ArtistGenreDialogController extends BaseDialogController<ArtistGenre> {
    
    private ArtistGenre artistGenre;
    
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ArtistGenreRepository artistGenreRepository;
    
    @FXML
    private ChoiceBox<Resource<Genre>> genreChoiceBox;
    @FXML
    private ChoiceBox<Resource<Artist>> artistChoiceBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initGenreChoiceBox();
        initArtistChoiceBox();
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
    
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) {
            artistGenre.setArtist(artistChoiceBox.getValue().getId().getHref());
            artistGenre.setGenre(genreChoiceBox.getValue().getId().getHref());           
            try { 
                if (edit) {
                    artistGenreRepository.update(resource);
                    artistGenreRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    artistGenreRepository.save(artistGenre);
                    artistGenreRepository.setAdded(new WrapChangedEntity<>(null, resource));
                }  
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }        
            dialogStage.close();
        }
    }
    
    @Override
    protected void add() {     
        artistGenre = (resource == null) ? new ArtistGenre() : resource.getContent();                
        selectArtistChoiceBox(artistGenre.getArtist());
        selectGenreChoiceBox(artistGenre.getGenre()); 
    }
    
    @Override
    protected void edit() {     
        edit = true;
        artistGenre = resource.getContent();
        oldResource = new Resource<>(artistGenre.clone(), resource.getLinks());       
        try {
            selectGenreChoiceBox(genreRepository.getResource(resource.getLink("genre").getHref()).getId().getHref());          
            selectArtistChoiceBox(artistRepository.getResource(resource.getLink("artist").getHref()).getId().getHref());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }              
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";                        
        try {                  
            if (artistChoiceBox.getValue().getId().getHref().equals(Artist.DEFAULT_ARTIST) 
                    && genreChoiceBox.getValue().getId().getHref().equals(Genre.DEFAULT_GENRE)) {
                errorMessage += "Выберите группу или жанр из списка \n";
            } 
            else if (!edit && artistGenreRepository.countByArtistAndGenre(artistChoiceBox.getValue(), genreChoiceBox.getValue()) > 0) {
                errorMessage += "Такой жанр уже есть у артиста \n";
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
