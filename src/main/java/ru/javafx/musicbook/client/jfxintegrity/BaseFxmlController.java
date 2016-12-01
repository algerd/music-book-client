
package ru.javafx.musicbook.client.jfxintegrity;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/*
    Пример аннотации контроллеров:

        @FXMLController("/fxml/somepath/someview.fxml")
        @FXMLController(
            value = "/fxml/somepath/main.fxml", // fxml-view path
            css = ({"/styles/somepath/style1.css", "/fxml/somepath/style2.css"}), // array css pathes
            bundle = ("..."),
            tittle = "Some Controller"
        }
    Если не задавать пути вьюхи и css, то они будут искаться соответственно в папках /fxml/ и /styles/ по
    имени контроллера без суффикса controller: SomeController будет искать /fxml/Some.fxml и /styles/Some.css

*/
public abstract class BaseFxmlController implements Loadable, Initializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String FXML_PATH_ROOT = "/fxml/";
    private static final String CSS_PATH_ROOT = "/styles/";
    protected Parent view;
    protected URL fxmlPath; 
    protected ResourceBundle resourceBundle;
    protected FXMLLoader fxmlLoader;
    protected StringProperty title = new SimpleStringProperty();
    
    @Autowired
    private ControllerFactory controllerFactory;
    
    public BaseFxmlController() {  
        fxmlPath = getClass().getResource(getStringFxmlPath());      
		resourceBundle = getResourceBundle(getBundleName());
        title.set(getFxmlAnnotation().title());
	}
       
    @Override
    public Parent getView() {
        if (view == null && getFxmlAnnotation().loadable()) {
            view = loadView();
        }
        logger.info("View: {}", view);
        return view;
    }
    
    // Используется для инклудных вьюх, в которых загрузка вида происходит автоматически.
    // Этот метод нужен для инициализации стилей для инклудного вида.
    @Override
    public void setView(Parent view) {
        if (!getFxmlAnnotation().loadable()) {
            this.view = view;
            addCss(this.view);
        }
    }
    
    private Parent loadView() {
		if (fxmlLoader == null) {
			fxmlLoader = load(fxmlPath, resourceBundle);
		}
		Parent parent = fxmlLoader.getRoot();
		addCss(parent);
		return parent;
	}
          
    private FXMLLoader load(URL resource, ResourceBundle bundle) throws IllegalStateException {
		FXMLLoader loader = new FXMLLoader(resource, bundle);
        loader.setControllerFactory(controllerFactory);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new IllegalStateException("Cannot load " + getConventionalName(), ex);
		}
		return loader;
	}
    
    private void addCss(Parent parent) {
        FXMLController annotation = getFxmlAnnotation();
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
       
    private String getStringFxmlPath() {
        FXMLController annotation = getFxmlAnnotation();
        return annotation == null || annotation.value().equals("") ?
            FXML_PATH_ROOT + getConventionalName(".fxml") : annotation.value();
    }
    
    private FXMLController getFxmlAnnotation() {
		return getClass().getAnnotation(FXMLController.class);
	}
    
    private String getConventionalName(String ending) {
		return getConventionalName() + ending;
	}

	private String getConventionalName() {
        String clazz = getClass().getSimpleName().toLowerCase();
        return !clazz.endsWith("controller") ? clazz : clazz.substring(0, clazz.lastIndexOf("controller"));
	}
    
    private String getBundleName() {
        FXMLController annotation = getFxmlAnnotation();
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
    
    @Override
    public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
    
    @Override
    public void setTitle(String title) {
	    this.title.set(title);
	}
    
    @Override
    public String getTitle() {
	    return title.get();
	}
	
    @Override
	public StringProperty titleProperty() {
	    return title;
	}
       
}
