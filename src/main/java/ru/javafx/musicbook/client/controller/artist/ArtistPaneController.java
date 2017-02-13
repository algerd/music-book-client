
package ru.javafx.musicbook.client.controller.artist;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.springframework.context.annotation.Scope;
import ru.javafx.musicbook.client.controller.EntityController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_ARTIST;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_ARTIST;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_ARTIST;

@FXMLController(
    value = "/fxml/artist/ArtistPane.fxml",    
    title = "Artist")
@Scope("prototype")
public class ArtistPaneController extends EntityController<Artist> {
    
    @FXML
    private GenreListController includedGenreListController;
   
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
        includedGenreListController.setPaneController(this);
        
        artistTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void showDetails() {   
        nameLabel.textProperty().bind(resource.getContent().nameProperty());                                 
        ratingLabel.textProperty().bind(resource.getContent().ratingProperty().asString());
        commentText.textProperty().bind(resource.getContent().descriptionProperty());  
        //artistImageView.setImage(ImageUtil.readImage(file));      
    }
    
    private void initRepositoryListeners() {
        /*
        repositoryService.getArtistRepository().clearDeleteListeners(this);
        repositoryService.getArtistRepository().clearUpdateListeners(this);
       
        repositoryService.getArtistRepository().addDeleteListener(this::deletedArtist, this);
        repositoryService.getArtistRepository().addUpdateListener(this::updatedArtist, this);
        */
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
