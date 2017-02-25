
package ru.javafx.musicbook.client.controller.album;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
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
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.EntityController;
import ru.javafx.musicbook.client.controller.artist.ArtistPaneController;
import ru.javafx.musicbook.client.controller.genre.GenreListController;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_ALBUM;

@FXMLController(
    value = "/fxml/album/AlbumPane.fxml",    
    title = "Album")
@Scope("prototype")
public class AlbumPaneController extends EntityController<Album> {
    
    private Resource<Artist> artistResource; 
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private ArtistRepository artistRepository;
    
    @FXML
    private GenreListController includedGenreListController;  
    @FXML
    private MusicianTableController includedMusicianTableController;
    @FXML
    private SongTableController includedSongTableController;
    
    @FXML
    private TabPane albumTabPane;
    @FXML
    private Tab detailsTab; 
    @FXML
    private ImageView albumImageView;
    @FXML
    private Hyperlink artistLink; 
    @FXML
    private Label nameLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Text commentText;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }  
    
    @Override
    public void show() {
        showDetails();
        initRepositoryListeners();
        includedGenreListController.bootstrap(resource);
        includedMusicianTableController.bootstrap(this);
        includedSongTableController.bootstrap(this);
        albumTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void showDetails() {   
        try { 
            artistResource = artistRepository.getResource(resource.getLink("artist").getHref());
            artistLink.textProperty().bind(artistResource.getContent().nameProperty());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
        nameLabel.textProperty().bind(resource.getContent().nameProperty());
        yearLabel.textProperty().bind(resource.getContent().yearProperty().asString());
        timeLabel.textProperty().bind(resource.getContent().timeProperty());
        ratingLabel.textProperty().bind(resource.getContent().ratingProperty().asString());
        commentText.textProperty().bind(resource.getContent().descriptionProperty());      
        showImage();       
    }
    
    private void showImage() {
        if (resource.hasLink("get_image")) {
            albumImageView.setImage(new Image(resource.getLink("get_image").getHref()));  
        } 
    }
    
    private void initRepositoryListeners() {    
        albumRepository.clearDeleteListeners(this);
        albumRepository.clearUpdateListeners(this);
               
        albumRepository.addDeleteListener(this::deletedAlbum, this);
        albumRepository.addUpdateListener(this::updatedAlbum, this);
    }
       
    private void deletedAlbum(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Album> oldResource = ((WrapChangedEntity<Resource<Album>>) oldVal).getOld();
        if (oldResource.getId().equals(resource.getId())) {
            view.setVisible(false);
        }
    }
    
    private void updatedAlbum(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Album> newResource = ((WrapChangedEntity<Resource<Album>>) newVal).getNew();
        if (newResource.getId().equals(resource.getId())) {         
            includedGenreListController.bootstrap(resource);
            showImage();
        } 
    }
    
    @FXML 
    private void onLinkArtist() {
        requestViewService.show(ArtistPaneController.class, artistResource);
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {    
            Album newAlbum = new Album();
            newAlbum.setArtist(artistResource.getId().getHref());
            contextMenuService.add(ADD_ALBUM, new Resource<>(newAlbum, new Link("null")));
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (resource != null && !resource.getId().getHref().equals(Album.DEFAULT_ALBUM)) {
                contextMenuService.add(EDIT_ALBUM, resource);
                contextMenuService.add(DELETE_ALBUM, resource);                       
            }
            contextMenuService.show(view, mouseEvent);
        }       
    }

}
