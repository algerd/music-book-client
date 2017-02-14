
package ru.javafx.musicbook.client.controller.artist;

import ru.javafx.musicbook.client.controller.genre.GenreListController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import ru.javafx.musicbook.client.controller.EntityController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_ARTIST;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_ARTIST;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_ARTIST;

@FXMLController(
    value = "/fxml/artist/ArtistPane.fxml",    
    title = "Artist")
@Scope("prototype")
public class ArtistPaneController extends EntityController<Artist> {
    
    @Autowired
    private ArtistRepository artistRepository;
    
    @FXML
    private GenreListController includedGenreListController;  
    @FXML
    private ArtistReferenceTableController includedArtistReferenceTableController;
    @FXML
    private TabPane artistTabPane;
    @FXML
    private Tab detailsTab;         
    @FXML
    private ImageView artistImageView;
    @FXML
    private Label nameLabel;       
    @FXML
    private Label ratingLabel;  
    @FXML
    private Text commentText;   

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    @Override
    public void show() {
        showDetails();
        initRepositoryListeners();
        includedGenreListController.bootstrap(resource);
        
        artistTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void showDetails() {   
        nameLabel.textProperty().bind(resource.getContent().nameProperty());                                 
        ratingLabel.textProperty().bind(resource.getContent().ratingProperty().asString());
        commentText.textProperty().bind(resource.getContent().descriptionProperty());  
        //artistImageView.setImage(ImageUtil.readImage(file));      
    }
    
    private void initRepositoryListeners() {
        artistRepository.clearDeleteListeners(this);
        artistRepository.clearUpdateListeners(this);
        
        artistRepository.addDeleteListener(this::deletedArtist, this);
        artistRepository.addUpdateListener(this::updatedArtist, this);
    }
    
    private void deletedArtist(ObservableValue observable, Object oldVal, Object newVal) {
        /*
        ArtistEntity newEntity = ((WrapChangedEntity<ArtistEntity>) newVal).getNew();
        if (newEntity.getId() == artist.getId()) {
            view.setVisible(false);
        }
        */
    }
    
    private void updatedArtist(ObservableValue observable, Object oldVal, Object newVal) {
        /*
        if (file.lastModified() != imageLastModified) {
            artistImageView.setImage(ImageUtil.readImage(file));
            imageLastModified = file.lastModified();
        }
        */
        includedGenreListController.bootstrap(resource);       
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_ARTIST, null);
            // запретить удаление и редактирование записи с id = 1 (Unknown artist)
            if (resource != null && !resource.getId().getHref().equals(Artist.DEFAULT_ARTIST)) {
                contextMenuService.add(EDIT_ARTIST, resource);
                contextMenuService.add(DELETE_ARTIST, resource);                       
            }
            contextMenuService.show(view, mouseEvent);
        }      
    }

}
