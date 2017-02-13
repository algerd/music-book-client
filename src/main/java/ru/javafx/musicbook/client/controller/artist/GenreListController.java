package ru.javafx.musicbook.client.controller.artist;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseAwareController;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/artist/GenreList.fxml")
@Scope("prototype")
public class GenreListController extends BaseAwareController {
    
    protected ArtistPaneController paneController;
    
    @Autowired
    private GenreRepository genreRepository;
    
    public void setPaneController(ArtistPaneController paneController) {
        this.paneController = paneController;
        bootstrap();
    }
    
    @FXML
    private AnchorPane genreList;
    @FXML
    private ListView<Resource<Genre>> genreListView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        genreListView.setCellFactory((ListView<Resource<Genre>> listView) -> new ListCell<Resource<Genre>>() {
            @Override
            public void updateItem(Resource<Genre> item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    this.setText(item.getContent().getName());
                }
            }
        });
    }
    
    public void bootstrap() {
        setListValue();
        initRepositoryListeners();     
    }
    
    private void setListValue() {               
        List <Resource<Genre>> genreResources = new ArrayList<>();
        genreListView.getItems().clear();
        try {
            genreResources.addAll(genreRepository.findByArtist(paneController.getResource()).getContent().parallelStream().collect(Collectors.toList()));                     
            if (!genreResources.isEmpty()) {
                genreListView.getItems().addAll(genreResources);
                sort();
            } else {
                Genre emptyGenre = new Genre();
                emptyGenre.setName("Unknown");
                Resource<Genre> resorceGenre = new Resource<>(emptyGenre, new Link(Genre.DEFAULT_GENRE));
                genreListView.getItems().add(resorceGenre);
            }                     
            Helper.setHeightList(genreListView, 8);  
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }     
    }
    
    private void initRepositoryListeners() {
        //repositoryService.getArtistGenreRepository().clearChangeListeners(this);
        genreRepository.clearChangeListeners(this);
        
        //repositoryService.getArtistGenreRepository().addChangeListener((observable, oldVal, newVal) -> setListValue(), this);
        genreRepository.addChangeListener((observable, oldVal, newVal) -> setListValue(), this);
    }
   
    private void sort() {
        genreListView.getSelectionModel().clearSelection();
        genreListView.getItems().sort(Comparator.comparing(resource -> resource.getContent().getName()));
    }  
   
    @FXML
    private void onMouseClickGenreList(MouseEvent mouseEvent) { 
        contextMenuService.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {           
            Resource<Genre> selectedItem = genreListView.getSelectionModel().getSelectedItem();
            // если лкм выбрана запись - показать её
            if (selectedItem != null && !selectedItem.getId().getHref().equals(Genre.DEFAULT_GENRE)) {
                //requestPageService.genrePane(genre);
            }           
        }      
    }
    
}
