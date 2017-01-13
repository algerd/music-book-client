
package ru.javafx.musicbook.client.controller.album;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import org.springframework.context.annotation.Scope;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;

@FXMLController(
    value = "/fxml/album/AlbumDialog.fxml",    
    title = "Album Dialog Window")
@Scope("prototype")
public class AlbumDialogController extends BaseDialogController<Album> {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        
    }
    
    @Override
    protected boolean isInputValid() {
        return true;
    }
    
    @Override
    protected void add() {
        
    }
       
    @Override
    protected void edit() { 
        
    }
    
    @FXML
    @Override
    protected void handleOkButton() {
        
    }

}
