package ru.javafx.musicbook.client.controller.helper.inputImageBox;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.Params;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.service.RequestService;
import ru.javafx.musicbook.client.utils.ImageUtil;

@FXMLController(value = "/fxml/helper/inputImageBox/DialogImageBox.fxml")
@Scope("prototype")
public class DialogImageBoxController implements Initializable {
    // надо убрать это и сделать автоматическим определение формата изображения  
    private String imageFormat = Params.SAVED_IMAGE_FORMAT;
    private int width = Params.WIDTH_COVER;
    private int heigth = Params.HEIGTH_COVER;        
    private boolean changedImage = false;       
    private Stage stage;
    private final ContextMenu contextMenu = new ContextMenu();
    
    @Autowired
    private RequestService requestService;
    
    @FXML
    private AnchorPane dialogImageBox;
    @FXML
    private StackPane imagePane;
    @FXML
    private ImageView imageView;
    @FXML
    private ProgressIndicator imageProgress;
    @FXML
    private TextFlow imageTextFlow;
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageProgress.setVisible(false);
    } 
       
    @FXML
    private void dragOverImage(DragEvent event) {
        ImageUtil.dragOverImage(event);
    }
    
    @FXML
    private void dragDroppedImage(DragEvent event) {        
        changedImage = checkImage(ImageUtil.dragDroppedImage(event));
    }
          
    @FXML
    private void pasteImage() {
        changedImage = checkImage(ImageUtil.pasteImage());
    }
    
    @FXML
    private void clearImage() {
        imageView.setImage(null);      
        imageProgress.progressProperty().unbind();
        imageProgress.visibleProperty().unbind();
        imageProgress.setVisible(false);
        imageTextFlow.setVisible(true);
        changedImage = true;
    }
    
    @FXML
    private void loadImage() {     
        changedImage = setImage(ImageUtil.openFileChooser());
    }
         
    private boolean checkImage(Image image) {
        if (image != null && !image.isError()) {
            if (image.isBackgroundLoading()) {
                imageProgress.visibleProperty().bind(image.progressProperty().isNotEqualTo(1.0, 0.001));
                imageProgress.progressProperty().bind(image.progressProperty());
            } else {
                imageProgress.setVisible(false);
            }
            imageView.setImage(image);
            imageTextFlow.setVisible(false);
            return true;
        }
        else {
            String errorMessage = "Command does not contain an image in the expected format: Image, Image File, Image URL";
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);           
            alert.showAndWait(); 
            return false;
        }
    }
        
    public File getImageFile() {  
        /*
        String nameEntity = entity.getClass().getSimpleName().toLowerCase();
        String dirImage = Params.DIR_IMAGE_ENTITY + nameEntity +"/"; 
        File file = new File(dirImage + entity.getId() + "." + imageFormat); 
        return file; 
        */
        return null;
    }
    
    public boolean setImage(File file) {
        if (file != null && Files.exists(file.toPath())) {
            imageView.setImage(ImageUtil.readImage(file));
            imageTextFlow.setVisible(false);
            return true;
        }
        return false;
    }
    
    public void setImage(String strUrl) {
        imageView.setImage(new Image(strUrl, true));
        imageTextFlow.setVisible(false);
    }

    public void saveImage(Resource<? extends Entity> resource) {     
        Image image = imageView.getImage();                     
        if (image != null) {
            requestService.postImage(resource, image, imageFormat);
        } else {
            //ImageUtil.deleteImage(imageFile);
        }
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenu.getItems().clear();
        if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MenuItem loadItem = new MenuItem("Load Image");
			MenuItem pasteItem = new MenuItem("Paste Image, File or URL");
            MenuItem clearItem = new MenuItem("Clear Image");
            contextMenu.getItems().addAll(loadItem, pasteItem, clearItem);
            contextMenu.show(imagePane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            
            loadItem.setOnAction(e -> {
                loadImage();
                contextMenu.getItems().clear();
            });
            pasteItem.setOnAction(e -> {
                pasteImage();
                contextMenu.getItems().clear();
            });          
            clearItem.setOnAction(e -> {
                clearImage();
                contextMenu.getItems().clear();
            });
        }    
    }  

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }
    
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeigth() {
        return heigth;
    }
    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }
    
    public boolean isChangedImage() {
        return changedImage;
    }
    public void setChangedImage(boolean changedImage) {
        this.changedImage = changedImage;
    }

}
