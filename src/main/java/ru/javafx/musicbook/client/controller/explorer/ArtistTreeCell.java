
package ru.javafx.musicbook.client.controller.explorer;

import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.hateoas.Resource;
import static ru.javafx.musicbook.client.Params.DIR_IMAGES;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.datacore.Entity;
import ru.javafx.musicbook.client.entity.Song;

public class ArtistTreeCell extends TreeCell<Resource<? extends Entity>> {
    
    @Override
    public void updateItem(Resource<? extends Entity> item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            textProperty().unbind();
            setText(null);
            setGraphic(null);
        } else {
            if (item.getContent() instanceof Artist) {
                textProperty().bind(((Resource<Artist>) item).getContent().nameProperty());
                setGraphic(getIcon("folder.jpg"));
            }
            else if (item.getContent() instanceof Album) {
                textProperty().bind(((Resource<Album>) item).getContent().nameProperty());
                setGraphic(getIcon("file.jpg"));
            }
            else if (item.getContent() instanceof Song) {
                this.textProperty().bind(((Resource<Song>) item).getContent().nameProperty());
                //this.setGraphic(getIcon("file.jpg"));
            }
        }
    }
    
    private ImageView getIcon(String fileName) {
		ImageView imgView = null;
		try {
			String imagePath = DIR_IMAGES + fileName;			
			Image img = new Image(imagePath);
			imgView = new ImageView(img);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return imgView;
	}    
}
