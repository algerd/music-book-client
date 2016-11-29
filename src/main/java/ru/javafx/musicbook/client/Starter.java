package ru.javafx.musicbook.client;

import javafx.scene.image.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.javafx.musicbook.client.controller.MainController;
import ru.javafx.musicbook.client.jfxintegrity.BaseSpringBootJavaFxApplication;
import ru.javafx.musicbook.client.service.RequestViewService;

@SpringBootApplication
public class Starter extends BaseSpringBootJavaFxApplication {
	
	public static void main(String[] args) {
		launchApp(Starter.class, MainController.class, args);
	}
    
    @Autowired
    private RequestViewService requestViewService;

    @Override
    public void show() {
        //requestViewService.show(Item2Controller.class);
        primaryStage.getIcons().add(new Image("images/icon_root_layout.png"));        
    }
	
}
