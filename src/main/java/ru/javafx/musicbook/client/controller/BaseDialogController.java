
package ru.javafx.musicbook.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.datacore.entity.Entity;

public abstract class BaseDialogController<T extends Entity> extends BaseAwareController implements DialogController<T> {
    
    protected Stage dialogStage;
    protected boolean edit;
    protected Resource<T> resource;
    protected Resource<T> oldResource;
    
    @Override
    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @Override
    public void setResource(Resource<T> resource) { 
        this.resource = resource;
        if (resource == null || !resource.hasLink("self") || resource.getLink("self").getHref().equals("null")) {
            dialogStage.setTitle("Add");
            add();
        } else {
            dialogStage.setTitle("Edit"); 
            edit();
        }      
    }
    
    protected void errorMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Invalid Fields");
        alert.setHeaderText("Please correct invalid fields");
        alert.setContentText(errorMessage);           
        alert.showAndWait();  
    }
    
    @FXML
    protected void handleCancelButton() {
        dialogStage.close();
    }    
    
    protected abstract void add();
    
    protected abstract void edit();
    
    protected abstract boolean isInputValid();
    
    protected abstract void handleOkButton();
       
}
