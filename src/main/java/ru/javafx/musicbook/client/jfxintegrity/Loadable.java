
package ru.javafx.musicbook.client.jfxintegrity;

import java.util.ResourceBundle;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;

public interface Loadable {
      
    Parent getView();
       
    // only for included views with @FXMLController(loadable = false)
    void setView(Parent view);
    
    ResourceBundle getResourceBundle();
    
    void setTitle(String title);     
    String getTitle();	
	StringProperty titleProperty();
    
}
