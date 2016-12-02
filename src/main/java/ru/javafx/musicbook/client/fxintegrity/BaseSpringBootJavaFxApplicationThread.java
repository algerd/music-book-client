package ru.javafx.musicbook.client.fxintegrity;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

public abstract class BaseSpringBootJavaFxApplicationThread extends Application {
      
    private static Class<? extends BaseSpringBootJavaFxApplicationThread> appClass;
    private static String[] args;
    private static Class<? extends BaseFxmlController> mainController;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected ConfigurableApplicationContext springContext;
    protected Stage primaryStage;
    
    public abstract void show();
    
    @Bean("primaryStage")
    public Stage getStage() {
        Stage stage = new Stage(StageStyle.DECORATED);      
        return stage;
    }
               
    @Override
    public void start(Stage appStage) throws Exception {  
        Task<Object> worker = new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                springContext = SpringApplication.run(appClass, args);               
                return null;
            }
        };
        worker.run();        
        worker.setOnSucceeded(event -> showStage());
        worker.setOnFailed(event -> showAlert());
    }
      
    private void showStage() {
        springContext.getAutowireCapableBeanFactory().autowireBean(this);
        
        BaseFxmlController controller = springContext.getBean(FXMLControllerLoader.class).load(mainController);
        Scene scene = new Scene(controller.getView());
        primaryStage = springContext.getBean("primaryStage", Stage.class);
        primaryStage.titleProperty().bind(controller.titleProperty());
        primaryStage.setScene(scene); 
        show();
        primaryStage.show();
    }
    
    private void showAlert() {
        logger.info("Application force stoped!");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("Application force stoped!");
        alert.setContentText("Application force stoped!");
        alert.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        springContext.close();
    }

    protected static void launchApp(
            Class<? extends BaseSpringBootJavaFxApplicationThread> appClass,
			Class<? extends BaseFxmlController> mainController, 
            String[] args) {
        
        BaseSpringBootJavaFxApplicationThread.appClass = appClass;
		BaseSpringBootJavaFxApplicationThread.mainController = mainController;
		BaseSpringBootJavaFxApplicationThread.args = args;
		Application.launch(appClass, args);
	}

}
