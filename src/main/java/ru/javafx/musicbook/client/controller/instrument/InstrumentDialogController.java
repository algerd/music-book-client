
package ru.javafx.musicbook.client.controller.instrument;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.controller.helper.inputImageBox.DialogImageBoxController;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.datacore.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/instrument/InstrumentDialog.fxml",    
    title = "Instrument Dialog Window")
@Scope("prototype")
public class InstrumentDialogController extends BaseDialogController<Instrument> {
    
    private Instrument instrument;
    
    @Autowired
    private InstrumentRepository instrumentRepository;
    
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionTextArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        Helper.limitTextInput(nameField, 255);
        Helper.limitTextInput(descriptionTextArea, 1000);
        includedDialogImageBoxController.setStage(dialogStage);
    }
    
    @FXML
    @Override
    protected void handleOkButton() {
        if (isInputValid()) { 
            instrument.setName( nameField.getText() != null ? nameField.getText().trim() : "");
            instrument.setDescription(descriptionTextArea.getText() != null ? descriptionTextArea.getText().trim() : "");  
            try {  
                resource = edit ? instrumentRepository.update(resource) : instrumentRepository.saveAndGetResource(instrument);                
                logger.info("Saved Instrument Resource: {}", resource);
                if (includedDialogImageBoxController.isChangedImage()) {
                    instrumentRepository.saveImage(resource, includedDialogImageBoxController.getImage());
                    includedDialogImageBoxController.setChangedImage(false);                              
                }  
                if (edit) {
                    instrumentRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    instrumentRepository.setAdded(new WrapChangedEntity<>(null, resource));
                } 
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
                //ex.printStackTrace();
            }    
            dialogStage.close();
            edit = false;
        }
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = ""; 
        String text = nameField.getText();      
        if (text == null || text.trim().equals("")) {
            errorMessage += "Введите название инструмента!\n"; 
        } else {
            text = text.trim().toLowerCase();
            try {
                if (!instrument.getName().toLowerCase().equals(text) 
                        && instrumentRepository.getPagedResources("name=" + text).getMetadata().getTotalElements() > 0) {
                    errorMessage += "Такой жанр уже есть!\n";
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
    
    @Override
    protected void add() {
        instrument = new Instrument();
    }
       
    @Override
    protected void edit() { 
        edit = true;
        instrument = resource.getContent(); 
        oldResource = new Resource<>(instrument.clone(), resource.getLinks());
        nameField.setText(instrument.getName());
        descriptionTextArea.setText(instrument.getDescription());
        if (resource.hasLink("get_image")) {
            includedDialogImageBoxController.setImage(resource.getLink("get_image").getHref()); 
        }
    }
    
}
