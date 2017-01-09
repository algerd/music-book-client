
package ru.javafx.musicbook.client.controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Entity;

public abstract class BaseDialogController<T extends Entity> extends BaseAwareController implements DialogController<T> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
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
