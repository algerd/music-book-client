package ru.javafx.musicbook.client.controller;

import javafx.stage.Stage;
import ru.javafx.musicbook.client.entity.IdAware;

public interface DialogController {
    
    void setStage(Stage stage);
    
    void setEntity(IdAware entity);
         
}
