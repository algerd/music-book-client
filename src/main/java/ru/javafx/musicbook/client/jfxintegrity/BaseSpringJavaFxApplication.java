
package ru.javafx.musicbook.client.jfxintegrity;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class BaseSpringJavaFxApplication extends Application {
    
    private static Class<? extends BaseFxmlController> mainController;  
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected static AnnotationConfigApplicationContext springContext;
    protected Stage primaryStage;
      
    public abstract void show();
    
    @Override
    public void start(Stage stage) throws Exception {  
        primaryStage = stage;
        springContext.getBeanFactory().registerSingleton("primaryStage", primaryStage);
        springContext.getAutowireCapableBeanFactory().autowireBean(this);
        
        BaseFxmlController controller = springContext.getBean(mainController);    
        Scene scene = new Scene(controller.getView());  
        primaryStage.titleProperty().bind(controller.titleProperty());
        primaryStage.setScene(scene); 
        show();
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        springContext.close();
    }
    
    protected static void launchApp(
            Class<? extends BaseSpringJavaFxApplication> appClass,
			Class<? extends BaseFxmlController> mainController, 
            AnnotationConfigApplicationContext springContext,
            String[] args) {
        
        BaseSpringJavaFxApplication.springContext = springContext;
		BaseSpringJavaFxApplication.mainController = mainController;
		Application.launch(appClass, args);
	}

}
