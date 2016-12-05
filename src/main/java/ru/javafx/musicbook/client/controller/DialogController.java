package ru.javafx.musicbook.client.controller;

import javafx.stage.Stage;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Entity;

public interface DialogController {
    
    void setStage(Stage stage);
    
    void setResource(Resource<? extends Entity> resource);
         
}
