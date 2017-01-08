
package ru.javafx.musicbook.client.controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Entity;

public abstract class BaseDialogController extends BaseAwareController implements DialogController {
     
    protected Stage dialogStage;
    protected boolean edit;
    protected Resource<? extends Entity> resource;
    protected Resource<? extends Entity> oldResource;
    
    @Override
    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @Override
    public void setResource(Resource<? extends Entity> resource) { 
        this.resource = resource;
        if (resource != null) {           
            dialogStage.setTitle("Edit"); 
            edit();
        } 
        else {
            dialogStage.setTitle("Add");
            add();
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
    
    protected abstract void add();
    
    protected abstract void edit();
    
    protected abstract boolean isInputValid();
    
}
