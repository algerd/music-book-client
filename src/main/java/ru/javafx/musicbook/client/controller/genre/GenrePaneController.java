
package ru.javafx.musicbook.client.controller.genre;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.EntityController;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.datacore.impl.WrapChangedEntity;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_GENRE;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_GENRE;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_GENRE;

@FXMLController(
    value = "/fxml/genre/GenrePane.fxml",    
    title = "Genre")
@Scope("prototype")
public class GenrePaneController extends EntityController<Genre> {
    
    @Autowired
    private GenreRepository genreRepository;
    
    @FXML    
    private ArtistGenreTableController includedArtistGenreTableController;
    @FXML
    private AlbumGenreTableController includedAlbumGenreTableController;
    @FXML
    private SongGenreTableController includedSongGenreTableController;  
    @FXML
    private MusicianGenreTableController includedMusicianGenreTableController;
    @FXML
    private TabPane genreTabPane; 
    @FXML
    private Tab detailsTab;
    @FXML
    private ImageView genreImageView;
    @FXML
    private Label nameLabel;    
    @FXML
    private Text commentText;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {      
    }
    
    @Override
    public void show() {
        showDetails();
        initRepositoryListeners();
        includedArtistGenreTableController.bootstrap(this);
        includedAlbumGenreTableController.bootstrap(this);
        includedSongGenreTableController.bootstrap(this);
        includedMusicianGenreTableController.bootstrap(this);
        genreTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void showDetails() {
        nameLabel.textProperty().bind(resource.getContent().nameProperty()); 
        commentText.textProperty().bind(resource.getContent().descriptionProperty());
        showImage(); 
    }
    
    private void showImage() {
        if (resource.hasLink("get_image")) {
            genreImageView.setImage(new Image(resource.getLink("get_image").getHref()));  
        } 
    }
    
    private void initRepositoryListeners() {
        genreRepository.clearDeleteListeners(this);
        genreRepository.clearUpdateListeners(this);
        
        genreRepository.addDeleteListener(this::deletedGenre, this);
        genreRepository.addUpdateListener(this::updatedGenre, this);
    }
    
    private void deletedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Genre> oldResource = ((WrapChangedEntity<Resource<Genre>>) newVal).getOld();
        if (oldResource.getId().equals(resource.getId())) {
            view.setVisible(false);
        }
    }
    
    private void updatedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Genre> newResource = ((WrapChangedEntity<Resource<Genre>>) newVal).getNew();
        if (newResource.getId().equals(resource.getId())) {         
            showImage();
        } 
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenuService.add(ADD_GENRE, null);           
            // запретить удаление и редактирование записи с id = 1 (Unknown genre)
            if (resource != null && !resource.getId().getHref().equals(Genre.DEFAULT_GENRE)) {
                contextMenuService.add(EDIT_GENRE, resource);
                contextMenuService.add(DELETE_GENRE, resource);
            }
            contextMenuService.show(view, mouseEvent);            
        }      
    }

}
