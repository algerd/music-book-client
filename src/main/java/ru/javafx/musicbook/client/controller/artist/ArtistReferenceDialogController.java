
package ru.javafx.musicbook.client.controller.artist;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.entity.ArtistReference;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistReferenceRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/artist/ArtistReferenceDialog.fxml",    
    title = "Artist Reference Dialog Window")
@Scope("prototype")
public class ArtistReferenceDialogController extends BaseDialogController<ArtistReference>  {
    
    private ArtistReference artistReference; 
    
    @Autowired
    private ArtistReferenceRepository artistReferenceRepository;
    
    @FXML
    private AnchorPane view;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField referenceTextField;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(referenceTextField, 255);
    }
    
    @Override
    protected void add() {  
        artistReference = resource.getContent();
    }
    
    @Override
    protected void edit() {    
        edit = true;
        artistReference = resource.getContent();
        oldResource = new Resource<>(artistReference.clone(), resource.getLinks());
        
        nameTextField.setText(artistReference.getName());
        referenceTextField.setText(artistReference.getReference());                             
    }
    
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) {
            artistReference.setName(nameTextField.getText().trim());
            artistReference.setReference(referenceTextField.getText() != null ? referenceTextField.getText().trim() : "");
            try { 
                if (edit) {
                    artistReferenceRepository.update(resource);
                    artistReferenceRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {                  
                    artistReferenceRepository.save(artistReference);
                    artistReferenceRepository.setAdded(new WrapChangedEntity<>(null, resource));
                }
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }    
            dialogStage.close();
        }
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        String text = nameTextField.getText();         
        if (text == null || text.trim().equals("")) {
            errorMessage += "Введите название ссылки!\n"; 
        } else {
            text = text.trim().toLowerCase();
            try {
                if (!artistReference.getName().toLowerCase().equals(text) && 
                        artistReferenceRepository.getPagedResources("name=" + text.trim().toLowerCase()).getMetadata().getTotalElements() > 0) {
                    errorMessage += "Такое название ссылки уже есть!\n";
                }
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            } 
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
