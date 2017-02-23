
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
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGenre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.MusicianGenreRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;

@FXMLController(
    value = "/fxml/genre/MusicianGenreDialog.fxml",    
    title = "Musician Genre Dialog Window")
@Scope("prototype")
public class MusicianGenreDialogController extends BaseDialogController<MusicianGenre> {
    
    private MusicianGenre musicianGenre;
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MusicianGenreRepository musicianGenreRepository;
    
    @FXML
    private ChoiceBox<Resource<Genre>> genreChoiceBox;
    @FXML
    private ChoiceBox<Resource<Musician>> musicianChoiceBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initGenreChoiceBox();
        initMusicianChoiceBox();
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
    
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) {
            musicianGenre.setMusician(musicianChoiceBox.getValue().getId().getHref());
            musicianGenre.setGenre(genreChoiceBox.getValue().getId().getHref());           
            try { 
                if (edit) {
                    musicianGenreRepository.update(resource);
                    musicianGenreRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    musicianGenreRepository.save(musicianGenre);
                    musicianGenreRepository.setAdded(new WrapChangedEntity<>(null, resource));
                }  
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }        
            dialogStage.close();
        }
    }
    
    @Override
    protected void add() {     
        musicianGenre = (resource == null) ? new MusicianGenre() : resource.getContent();                
        selectMusicianChoiceBox(musicianGenre.getMusician());
        selectGenreChoiceBox(musicianGenre.getGenre()); 
    }
    
    @Override
    protected void edit() {     
        edit = true;
        musicianGenre = resource.getContent();
        oldResource = new Resource<>(musicianGenre.clone(), resource.getLinks());       
        try {
            selectGenreChoiceBox(genreRepository.getResource(resource.getLink("genre").getHref()).getId().getHref());          
            selectMusicianChoiceBox(musicianRepository.getResource(resource.getLink("musician").getHref()).getId().getHref());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }              
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";                        
        try {                  
            if (musicianChoiceBox.getValue().getId().getHref().equals(Artist.DEFAULT_ARTIST) 
                    && genreChoiceBox.getValue().getId().getHref().equals(Genre.DEFAULT_GENRE)) {
                errorMessage += "Выберите музыканта или жанр из списка \n";
            } 
            else if (!edit && musicianGenreRepository.countByMusicianAndGenre(musicianChoiceBox.getValue(), genreChoiceBox.getValue()) > 0) {
                errorMessage += "Такой жанр уже есть у музыканта \n";
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
