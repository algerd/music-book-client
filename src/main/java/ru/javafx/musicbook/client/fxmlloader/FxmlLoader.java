
package ru.javafx.musicbook.client.fxmlloader;

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
public class FxmlLoader {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String FXML_PATH_ROOT = "/fxml/";
    private static final String CSS_PATH_ROOT = "/styles/";
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private BaseFxmlController load(Class<? extends BaseFxmlController> controllerClass) {       
        FXMLController annotation = getFxmlAnnotation(controllerClass);

        FXMLLoader fxmlLoader = load(
            controllerClass.getResource(getStringFxmlPath(annotation)),
            getResourceBundle(getBundleName(annotation))    
        );        
        
        Parent parent = fxmlLoader.getRoot();
		addCss(parent, annotation);
        
        BaseFxmlController controller = fxmlLoader.getController();      
        controller.setView(parent);
        controller.setTitle(annotation.title());
        
        return controller;      
    }

    private FXMLLoader load(URL resource, ResourceBundle bundle) throws IllegalStateException {
		FXMLLoader loader = new FXMLLoader(resource, bundle);
        loader.setControllerFactory(applicationContext::getBean);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new IllegalStateException("Cannot load " + getConventionalName(), ex);
		}
		return loader;
	}
    
    private String getStringFxmlPath(FXMLController annotation) {
        return annotation == null || annotation.value().equals("") ?
            FXML_PATH_ROOT + getConventionalName(".fxml") : annotation.value();
    }
    
    private FXMLController getFxmlAnnotation(Class<? extends BaseFxmlController> controllerClass) {
        return controllerClass.getAnnotation(FXMLController.class);
	}
    
    private String getConventionalName(String ending) {
		return getConventionalName() + ending;
	}

	private String getConventionalName() {
        String clazz = getClass().getSimpleName().toLowerCase();
        return !clazz.endsWith("controller") ? clazz : clazz.substring(0, clazz.lastIndexOf("controller"));
	}
    
    private String getBundleName(FXMLController annotation) {
        return (annotation == null || annotation.bundle().equals("")) ?
            getClass().getPackage().getName() + "." + getConventionalName() :    
            annotation.bundle();    
	}
    
	private ResourceBundle getResourceBundle(String name) {
		try {
			return ResourceBundle.getBundle(name);
		} catch (MissingResourceException ex) {
			return null;
		}
	}
    
    private void addCss(Parent parent, FXMLController annotation) {
		if (annotation != null && annotation.css().length > 0) {
			for (String cssFile : annotation.css()) {
				parent.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
			}
		}
        else {
            URL uri = getClass().getResource(CSS_PATH_ROOT + getConventionalName(".css"));
            if (uri != null) {
                parent.getStylesheets().add(uri.toExternalForm());
            }
        }    
	}
    
}
