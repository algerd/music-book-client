
package ru.javafx.musicbook.client.service;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import ru.javafx.musicbook.client.controller.DialogController;
import ru.javafx.musicbook.client.controller.MainController;
import ru.javafx.musicbook.client.fxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;
import ru.javafx.musicbook.client.entity.Entity;

@Service
public class RequestViewService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private FXMLControllerLoader fxmlLoader;
    
    @Autowired
    private MainController mainController;
    
    @Autowired
    private ApplicationContext applicationContext;
   
    public void show(Class<? extends BaseFxmlController> controllerClass) {
        mainController.show(fxmlLoader.load(controllerClass));        
    }
    
    // вызов диалогового окна
    public void showDialog(Class<? extends BaseFxmlController> controllerClass, Modality modality) {       
        Stage stage = new Stage();           
        stage.initModality(modality);
        stage.initOwner(applicationContext.getBean("primaryStage", Stage.class));
        Scene scene = new Scene(fxmlLoader.load(controllerClass).getView()); 
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    public void showDialog(Class<? extends BaseFxmlController> controllerClass) {
        showDialog(controllerClass, Modality.WINDOW_MODAL);
    }
    
    // вызов диалогового окна c передачей сущности в контроллер диалога
    public void showDialog(Class<? extends BaseFxmlController> controllerClass, Resource<? extends Entity> resource, Modality modality, double width, double heigth) {       
        Stage stage = new Stage();           
        stage.initModality(modality);
        stage.initOwner(applicationContext.getBean("primaryStage", Stage.class));    
        BaseFxmlController controller = fxmlLoader.load(controllerClass);
        Scene scene = new Scene(controller.getView()); 
        stage.setScene(scene);
        if (width > 0) {
            stage.setMinHeight(width);
        }
        if (heigth > 0) {
            stage.setMinWidth(heigth);
        }          
        if (controller instanceof DialogController) {
            DialogController dialogController = (DialogController) controller;
            dialogController.setStage(stage);
            dialogController.setResource(resource);           
        }          
        stage.showAndWait();
    }
    
    public void showDialog(Class<? extends BaseFxmlController> controllerClass, Resource<? extends Entity> resource, double width, double heigth) {
        showDialog(controllerClass, resource, Modality.WINDOW_MODAL, width, heigth);
    }
    
    public void showDialog(Class<? extends BaseFxmlController> controllerClass, Resource<? extends Entity> resource) {
        showDialog(controllerClass, resource, Modality.WINDOW_MODAL, -1, -1);
    }
    
}
