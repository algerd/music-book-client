
package ru.javafx.musicbook.client.controller.musician;

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
import ru.javafx.musicbook.client.controller.genre.GenreListController;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.datacore.repository.impl.WrapChangedEntity;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_MUSICIAN;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_MUSICIAN;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_MUSICIAN;

@FXMLController(
    value = "/fxml/musician/MusicianPane.fxml",    
    title = "Musician")
@Scope("prototype")
public class MusicianPaneController extends EntityController<Musician> {
    
    @Autowired
    private MusicianRepository musicianRepository;

    @FXML
    private GenreListController includedGenreListController;
    @FXML
    private InstrumentListController includedInstrumentListController;
    @FXML
    private ArtistTableController includedArtistTableController;
    @FXML
    private AlbumTableController includedAlbumTableController;
    @FXML
    private SongTableController includedSongTableController; 

    @FXML
    private TabPane musicianTabPane;
    @FXML
    private Tab detailsTab;   
    @FXML
    private ImageView musicianImageView;
    @FXML
    private Label nameLabel; 
    @FXML
    private Label dobLabel; 
    @FXML
    private Label dodLabel; 
    @FXML
    private Label countryLabel; 
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
        includedInstrumentListController.bootstrap(resource);
        includedArtistTableController.bootstrap(this);
        includedAlbumTableController.bootstrap(this);
        includedSongTableController.bootstrap(this);
        musicianTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void showDetails() {
        nameLabel.textProperty().bind(resource.getContent().nameProperty());       
        dobLabel.textProperty().bind(resource.getContent().dateOfBirthProperty());  
        dodLabel.textProperty().bind(resource.getContent().dateOfDeathProperty()); 
        countryLabel.textProperty().bind(resource.getContent().countryProperty());
        ratingLabel.textProperty().bind(resource.getContent().ratingProperty().asString());
        commentText.textProperty().bind(resource.getContent().descriptionProperty());
        showImage(); 
    }
    
    private void showImage() {
        if (resource.hasLink("get_image")) {
            musicianImageView.setImage(new Image(resource.getLink("get_image").getHref()));  
        } 
    }
    
    private void initRepositoryListeners() {
        musicianRepository.clearDeleteListeners(this);
        musicianRepository.clearUpdateListeners(this);

        musicianRepository.addDeleteListener(this::deletedMusician, this);
        musicianRepository.addUpdateListener(this::updatedMusician, this);
    }
    
    private void deletedMusician(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Genre> oldResource = ((WrapChangedEntity<Resource<Genre>>) newVal).getOld();
        if (oldResource.getId().equals(resource.getId())) {
            view.setVisible(false);
        }
    }
    
    private void updatedMusician(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Genre> newResource = ((WrapChangedEntity<Resource<Genre>>) newVal).getNew();
        if (newResource.getId().equals(resource.getId())) {         
            showImage();
        } 
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenuService.add(ADD_MUSICIAN, null);           
            // запретить удаление и редактирование записи с id = 1 (Unknown musician)
            if (resource != null && !resource.getId().getHref().equals(Musician.DEFAULT_MUSICIAN)) {
                contextMenuService.add(EDIT_MUSICIAN, resource);
                contextMenuService.add(DELETE_MUSICIAN, resource);
            }
            contextMenuService.show(view, mouseEvent);            
        }      
    }
    
}
