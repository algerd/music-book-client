
package ru.javafx.musicbook.client.controller.instrument;

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
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_INSTRUMENT;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_INSTRUMENT;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_INSTRUMENT;

@FXMLController(
    value = "/fxml/instrument/InstrumentPane.fxml",    
    title = "Instrument")
@Scope("prototype")
public class InstrumentPaneController extends EntityController<Instrument> {
    
    @Autowired
    private InstrumentRepository instrumentRepository;
    
    @FXML
    private MusicianTableController includedMusicianTableController;
    @FXML
    private TabPane instrumentTabPane; 
    @FXML
    private Tab detailsTab; 
    @FXML
    private ImageView instrumentImageView;
    @FXML
    private Label nameLabel;    
    @FXML
    private Text descriptionText;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {      
    }
    
    @Override
    public void show() {
        showDetails();
        initRepositoryListeners();
        includedMusicianTableController.bootstrap(this);
        instrumentTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void showDetails() {
        nameLabel.textProperty().bind(resource.getContent().nameProperty()); 
        descriptionText.textProperty().bind(resource.getContent().descriptionProperty());
        showImage(); 
    }
    
    private void showImage() {
        if (resource.hasLink("get_image")) {
            instrumentImageView.setImage(new Image(resource.getLink("get_image").getHref()));  
        } 
    }
    
    private void initRepositoryListeners() {
        instrumentRepository.clearDeleteListeners(this);
        instrumentRepository.clearUpdateListeners(this);

        instrumentRepository.addDeleteListener(this::deletedInstrument, this);
        instrumentRepository.addUpdateListener(this::updatedInstrument, this);
    }
    
    private void deletedInstrument(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Genre> oldResource = ((WrapChangedEntity<Resource<Genre>>) newVal).getOld();
        if (oldResource.getId().equals(resource.getId())) {
            view.setVisible(false);
        }
    }
    
    private void updatedInstrument(ObservableValue observable, Object oldVal, Object newVal) {
        Resource<Genre> newResource = ((WrapChangedEntity<Resource<Genre>>) newVal).getNew();
        if (newResource.getId().equals(resource.getId())) {         
            showImage();
        } 
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenuService.add(ADD_INSTRUMENT, null);           
            // запретить удаление и редактирование записи с id = 1 (Unknown instrument)
            if (resource != null && !resource.getId().getHref().equals(Instrument.DEFAULT_INSTRUMENT)) {
                contextMenuService.add(EDIT_INSTRUMENT, resource);
                contextMenuService.add(DELETE_INSTRUMENT, resource);
            }
            contextMenuService.show(view, mouseEvent);            
        }      
    }

}
