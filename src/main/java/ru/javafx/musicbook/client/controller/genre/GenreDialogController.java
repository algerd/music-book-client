
package ru.javafx.musicbook.client.controller.genre;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.controller.helper.inputImageBox.DialogImageBoxController;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/genre/GenreDialog.fxml",    
    title = "Genre Dialog Window")
@Scope("prototype")
public class GenreDialogController extends BaseDialogController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Genre genre;
    
    @Autowired
    private GenreRepository genreRepository;
    
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    @FXML
    private TextField nameTextField;   
    @FXML
    private TextArea commentTextArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
        includedDialogImageBoxController.setStage(dialogStage);
    }
    
    @FXML
    private void handleOkButton() {
        if (isInputValid()) { 
            genre.setName(nameTextField.getText().trim());
            genre.setDescription(commentTextArea.getText().trim());           

            if (edit) {
                genreRepository.update(resource);               
            } else {
                resource = genreRepository.saveAndGetResource(genre); 
            }              
            if (includedDialogImageBoxController.isChangedImage()) {
                genreRepository.saveImage(resource, includedDialogImageBoxController.getImage());
                includedDialogImageBoxController.setChangedImage(false);                              
            }             
            dialogStage.close();
            edit = false;
        }
    }
    
    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
           
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        
        if (nameTextField.getText() == null || nameTextField.getText().trim().equals("")) {
            errorMessage += "Введите название жанра!\n"; 
        } 
        
        try {
            if (!genre.getName().equals(nameTextField.getText()) && genreRepository.existByName(nameTextField.getText())) {
                errorMessage += "Такой жанр уже есть!\n"; 
            }
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        
        if (errorMessage.equals("")) {
            return true;
        } 
        else {
            errorMessage(errorMessage);         
            return false;
        }
    }
      
    @Override
    protected void add() {
        genre = new Genre();
    }
       
    @Override
    protected void edit() { 
        edit = true;
        genre = (Genre) resource.getContent();      
        nameTextField.setText(genre.getName());
        commentTextArea.setText(genre.getDescription());
        if (resource.hasLink("get_image")) {
            includedDialogImageBoxController.setImage(resource.getLink("get_image").getHref()); 
        }
    }

}
