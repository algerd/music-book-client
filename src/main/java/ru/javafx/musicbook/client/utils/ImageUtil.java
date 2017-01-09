
package ru.javafx.musicbook.client.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ImageUtil {
    
    private static final Logger LOG = LogManager.getLogger(ImageUtil.class);
   
    public static Image readImage(File file) {	
        Image image = null;
		try {
            if (file != null && Files.exists(file.toPath())) {
                BufferedImage bufferedImage = ImageIO.read(file);
                image = SwingFXUtils.toFXImage(bufferedImage, null);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            LOG.error("Error: ", e);
        }
        return image;
    }

    public static File openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load an image file");
        // Filter only image files
		FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("PNG or JPEG Files", "*.png", "*.jpg", "*.jpeg");
		fileChooser.getExtensionFilters().addAll(extension);        
        return fileChooser.showOpenDialog(null);
    }
    
    /* *********************** Dragboard and Clipboard ************************** */  
    
    public static void dragOverImage(DragEvent event) {
		Dragboard dragboard = event.getDragboard();
		if (dragboard.hasImage() || dragboard.hasFiles() || dragboard.hasUrl()) {
			event.acceptTransferModes(TransferMode.ANY);
		}		
		event.consume();
    }
    
    public static Image dragDroppedImage(DragEvent event) { 
        Image image = null;
		Dragboard dragboard = event.getDragboard();	       
		if (dragboard.hasImage()) {
			image = dragboard.getImage();
		} else if (dragboard.hasFiles()) {
			image = transferImageFile(dragboard.getFiles());  
		} else if (dragboard.hasUrl()) {
            try {
                image = new Image(dragboard.getUrl(), true);
            } catch (Exception e) {}    
		} 
        event.consume();
        return image;      
    }
    
    public static Image pasteImage() {
        Image image = null;
        Clipboard clipboard = Clipboard.getSystemClipboard();      
        if (clipboard.hasImage()) {
			image = clipboard.getImage();
		} else if (clipboard.hasFiles()) {
			image = transferImageFile(clipboard.getFiles());
		} else if (clipboard.hasUrl()) {
            try {
                //return new Image(clipboard.getUrl(), true); // выдаёт ошибку, поэтому использую clipboard.getString()                
                image = new Image(clipboard.getString(), true);
            } catch (Exception e) {}
		}
        return image;
    }
    	
	private static Image transferImageFile(List<File> files) {
		// Look at the mime typeof all file. Use the first file having the mime type as "image/xxx"
		for(File file : files) {
			String mimeType;
			try {
				mimeType = Files.probeContentType(file.toPath());
				if (mimeType != null && mimeType.startsWith("image/")) {
					return new Image(file.toURI().toURL().toExternalForm());
				}
			} 
			catch (IOException e) {
				LOG.error("Error: ", e);
                //e.printStackTrace();
			}
		}		
		return null;
	}

}
