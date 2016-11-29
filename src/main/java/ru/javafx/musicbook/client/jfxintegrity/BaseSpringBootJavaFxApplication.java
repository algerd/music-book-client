package ru.javafx.musicbook.client.jfxintegrity;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class BaseSpringBootJavaFxApplication extends Application {
    
    private static Class<? extends BaseSpringBootJavaFxApplication> appClass;
    private static String[] args;
    private static Class<? extends BaseFxmlController> mainController;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected ConfigurableApplicationContext springContext;
    protected Stage primaryStage;
    
    public abstract void show();
    
    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(appClass);      
    }
    
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
            Class<? extends BaseSpringBootJavaFxApplication> appClass,
			Class<? extends BaseFxmlController> mainController, 
            String[] args) {
        
        BaseSpringBootJavaFxApplication.appClass = appClass;
		BaseSpringBootJavaFxApplication.mainController = mainController;
		BaseSpringBootJavaFxApplication.args = args;
		Application.launch(appClass, args);
	}
   
}
