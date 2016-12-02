
package ru.javafx.musicbook.client.service;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.javafx.musicbook.client.controller.DialogController;
import ru.javafx.musicbook.client.controller.MainController;
import ru.javafx.musicbook.client.entity.IdAware;
import ru.javafx.musicbook.client.fxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;

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
        logger.info("before show");
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
    public void showDialog(Class<? extends BaseFxmlController> controllerClass, IdAware entity, Modality modality, double width, double heigth) {       
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
            dialogController.setEntity(entity);
            dialogController.setStage(stage);
        }          
        stage.showAndWait();
    }
    
    public void showDialog(Class<? extends BaseFxmlController> controllerClass, IdAware entity, double width, double heigth) {
        showDialog(controllerClass, entity, Modality.WINDOW_MODAL, width, heigth);
    }
    
    public void showDialog(Class<? extends BaseFxmlController> controllerClass, IdAware entity) {
        showDialog(controllerClass, entity, Modality.WINDOW_MODAL, -1, -1);
    }
    
}
