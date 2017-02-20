
package ru.javafx.musicbook.client.controller.song;

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
import ru.javafx.musicbook.client.controller.album.AlbumPaneController;
import ru.javafx.musicbook.client.controller.artist.ArtistPaneController;
import ru.javafx.musicbook.client.controller.genre.GenreListController;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.SongRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_SONG;
import ru.javafx.musicbook.client.service.RequestViewService;

@FXMLController(
    value = "/fxml/song/SongPane.fxml",    
    title = "Song")
@Scope("prototype")
public class SongPaneController extends EntityController<Song> {
    
    private Resource<Artist> artistResource;
    private Resource<Album> albumResource;
    
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private RequestViewService requestViewService;
    
    @FXML
    private GenreListController includedGenreListController;
    @FXML
    private MusicianTableController includedMusicianTableController;
    @FXML
    private TabPane songTabPane;  
    @FXML
    private Tab detailsTab;
    @FXML
    private ImageView songImageView;
    @FXML
    private Hyperlink artistLink;       
    @FXML
    private Hyperlink albumLink; 
    @FXML
    private Label yearLabel;       
    @FXML
    private Label nameLabel;   
    @FXML
    private Label trackLabel;    
    @FXML
    private Label timeLabel;   
    @FXML
    private Label ratingLabel;
    @FXML
    private Text commentText;
    @FXML
    private Text lyricText;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    @Override
    public void show() {
        showDetails();
        initRepositoryListeners();
        includedGenreListController.bootstrap(resource);
        includedMusicianTableController.bootstrap(this);
        songTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void showDetails() {
        try { 
            albumResource = albumRepository.getResource(resource.getLink("album").getHref());
            albumLink.textProperty().bind(albumResource.getContent().nameProperty());
            yearLabel.textProperty().bind(albumResource.getContent().yearProperty().asString());
            artistResource = artistRepository.getResource(albumResource.getLink("artist").getHref());
            artistLink.textProperty().bind(artistResource.getContent().nameProperty());
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }       
        nameLabel.textProperty().bind(resource.getContent().nameProperty());
        trackLabel.textProperty().bind(resource.getContent().trackProperty().asString());
        timeLabel.textProperty().bind(resource.getContent().timeProperty());
        ratingLabel.textProperty().bind(resource.getContent().ratingProperty().asString());
        lyricText.textProperty().bind(resource.getContent().lyricProperty());
        commentText.textProperty().bind(resource.getContent().descriptionProperty());
        showImage();
    }
    
    private void showImage() {
        if (resource.hasLink("get_image")) {
            songImageView.setImage(new Image(resource.getLink("get_image").getHref()));  
        } 
    }
    
    @FXML 
    private void onLinkArtist() {  
        requestViewService.show(ArtistPaneController.class, artistResource);
    }
    
    @FXML 
    private void onLinkAlbum() {
        requestViewService.show(AlbumPaneController.class, albumResource);
    }
    
    private void initRepositoryListeners() {      
        songRepository.clearDeleteListeners(this);      
        songRepository.clearUpdateListeners(this); 
        
        songRepository.addDeleteListener(this::deletedSong, this);      
        songRepository.addUpdateListener(this::updatedSong, this);          
    }
    
    private void deletedSong(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Song> oldResource = ((WrapChangedEntity<Resource<Song>>) oldVal).getOld();
        if (oldResource.getId().equals(resource.getId())) {
            view.setVisible(false);
        }
    }
    
    private void updatedSong(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Song> newResource = ((WrapChangedEntity<Resource<Song>>) newVal).getNew();
        if (newResource.getId().equals(resource.getId())) {         
            includedGenreListController.bootstrap(resource);
            showImage();
        } 
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            Song newSong = new Song();
            newSong.setAlbum(albumResource.getId().getHref());           
            contextMenuService.add(ADD_SONG, new Resource<>(newSong, new Link("null")));
            contextMenuService.add(EDIT_SONG, resource);
            contextMenuService.add(DELETE_SONG, resource);        
            contextMenuService.show(view, mouseEvent);
        }      
    }

}
