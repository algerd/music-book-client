
package ru.javafx.musicbook.client.controller.genre;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseDialogController;
import ru.javafx.musicbook.client.controller.helper.inputImageBox.DialogImageBoxController;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/genre/GenreDialog.fxml",    
    title = "Genre Dialog Window")
@Scope("prototype")
public class GenreDialogController extends BaseDialogController<Genre> {

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
    @Override
    protected void handleOkButton() {
        if (isInputValid()) { 
            genre.setName(nameTextField.getText().trim());
            genre.setDescription(commentTextArea.getText().trim());  
            try { 
                if (edit) {
                    genreRepository.update(resource);               
                } else {
                    resource = genreRepository.saveAndGetResource(genre); 
                }              
                if (includedDialogImageBoxController.isChangedImage()) {
                    genreRepository.saveImage(resource, includedDialogImageBoxController.getImage());
                    includedDialogImageBoxController.setChangedImage(false);                              
                }  
                if (edit) {
                    genreRepository.setUpdated(new WrapChangedEntity<>(oldResource, resource));
                } else {
                    genreRepository.setAdded(new WrapChangedEntity<>(null, resource));
                } 
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
                //ex.printStackTrace();
            }    
            dialogStage.close();
            edit = false;
        }
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
            logger.error(ex.getMessage());
            //ex.printStackTrace();
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
        genre = resource.getContent(); 
        oldResource = new Resource<>(genre.clone(), resource.getLinks());
        nameTextField.setText(genre.getName());
        commentTextArea.setText(genre.getDescription());
        if (resource.hasLink("get_image")) {
            includedDialogImageBoxController.setImage(resource.getLink("get_image").getHref()); 
        }
    }

}
