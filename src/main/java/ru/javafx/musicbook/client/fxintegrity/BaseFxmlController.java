
package ru.javafx.musicbook.client.fxintegrity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFxmlController implements Initializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Parent view;
    protected StringProperty title = new SimpleStringProperty();

    public Parent getView() {
        return view;
    }

    public void setView(Parent view) {
        this.view = view;
    }
    
    public void setTitle(String title) {
	    this.title.set(title);
	}
    
    public String getTitle() {
	    return title.get();
	}
	
	public StringProperty titleProperty() {
	    return title;
	}

}
