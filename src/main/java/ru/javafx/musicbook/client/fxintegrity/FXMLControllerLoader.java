
package ru.javafx.musicbook.client.fxintegrity;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FXMLControllerLoader {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String FXML_PATH_ROOT = "/fxml/";
    private static final String CSS_PATH_ROOT = "/styles/";
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public BaseFxmlController load(Class<? extends BaseFxmlController> controllerClass) {       
        FXMLLoader fxmlLoader = load(
            controllerClass.getResource(getStringFxmlPath(controllerClass)),
            getResourceBundle(getBundleName(controllerClass))    
        );        
        
        Parent parent = fxmlLoader.getRoot();
		addCss(parent, controllerClass);
        
        BaseFxmlController controller = fxmlLoader.getController();      
        controller.setView(parent);
        controller.setTitle(getFxmlAnnotation(controllerClass).title());
        
        return controller;      
    }

    private FXMLLoader load(URL resource, ResourceBundle bundle) throws IllegalStateException {
        FXMLLoader loader = new FXMLLoader(resource, bundle);
        loader.setControllerFactory(applicationContext::getBean);
		try {
			loader.load();
		} catch (IOException ex) {
            //logger.error(ex.getMessage());
            ex.printStackTrace();
		}
		return loader;
	}
    
    private String getStringFxmlPath(Class<? extends BaseFxmlController> controllerClass) {
        FXMLController annotation = getFxmlAnnotation(controllerClass);
        return annotation == null || annotation.value().equals("") ?
            FXML_PATH_ROOT + getConventionalName(controllerClass, ".fxml") : annotation.value();
    }
    
    private FXMLController getFxmlAnnotation(Class<? extends BaseFxmlController> controllerClass) {
        return controllerClass.getAnnotation(FXMLController.class);
	}
    
    private String getConventionalName(Class<? extends BaseFxmlController> controllerClass, String ending) {
		return getConventionalName(controllerClass) + ending;
	}

	private String getConventionalName(Class<? extends BaseFxmlController> controllerClass) {
        String clazz = controllerClass.getSimpleName().toLowerCase();
        return !clazz.endsWith("controller") ? clazz : clazz.substring(0, clazz.lastIndexOf("controller"));
	}
    
    private String getBundleName(Class<? extends BaseFxmlController> controllerClass) {
        FXMLController annotation = getFxmlAnnotation(controllerClass);
        return (annotation == null || annotation.bundle().equals("")) ?
            controllerClass.getPackage().getName() + "." + getConventionalName(controllerClass) :    
            annotation.bundle();    
	}
    
	private ResourceBundle getResourceBundle(String name) {
		try {
			return ResourceBundle.getBundle(name);
		} catch (MissingResourceException ex) {
			return null;
		}
	}
    
    private void addCss(Parent parent, Class<? extends BaseFxmlController> controllerClass) {
        FXMLController annotation = getFxmlAnnotation(controllerClass);
		if (annotation != null && annotation.css().length > 0) {
			for (String cssFile : annotation.css()) {
				parent.getStylesheets().add(controllerClass.getResource(cssFile).toExternalForm());
			}
		}
        else {
            URL uri = controllerClass.getResource(CSS_PATH_ROOT + getConventionalName(controllerClass, ".css"));
            if (uri != null) {
                parent.getStylesheets().add(uri.toExternalForm());
            }
        }    
	}
    
}
