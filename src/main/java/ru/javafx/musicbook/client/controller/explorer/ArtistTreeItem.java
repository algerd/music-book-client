
package ru.javafx.musicbook.client.controller.explorer;

import java.net.URISyntaxException;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.service.RepositoryService;

public class ArtistTreeItem extends TreeItem<Resource<? extends Entity>> {
    
    private final RepositoryService repositoryService;
    
    private boolean childrenLoaded = false;
	private boolean leafPropertyComputed = false;
	private boolean leafNode = false;
    
    public ArtistTreeItem(Resource<? extends Entity> resource, RepositoryService repositoryService) {
		super(resource);	
        this.repositoryService = repositoryService;
	}
    
    @Override
	public ObservableList<TreeItem<Resource<? extends Entity>>> getChildren() {
		if (!childrenLoaded) {
			childrenLoaded = true;
			populateChildren();
		}
		return super.getChildren();
	}

	@Override 
	public boolean isLeaf() {
		if (!leafPropertyComputed) {
			leafPropertyComputed = true;
            if (getValue() == null) return leafNode;
            try {           
                if (getValue().getContent() instanceof Artist) {               
                    leafNode = !repositoryService.getAlbumRepository().findByArtist((Resource<Artist>) getValue()).iterator().hasNext();
                }
                else if (getValue().getContent() instanceof Album) {
                    leafNode = !repositoryService.getSongRepository().findByAlbum((Resource<Album>) getValue()).iterator().hasNext();          
                }
                else leafNode = getValue().getContent() instanceof Song;
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
		}
		return leafNode;
	}
    
    private void populateChildren() {
		getChildren().clear();
        if (getValue() == null) return;
        try {          
            if (getValue().getContent() instanceof Artist) {
                Resources<Resource<Album>> albums = repositoryService.getAlbumRepository().findByArtist((Resource<Artist>) getValue());
                //albums.sort(Comparator.comparingInt(AlbumEntity::getYear));
                for (Resource<Album> album : albums) {
                    getChildren().add(new ArtistTreeItem(album, repositoryService));            
                }
            }
            else if (getValue().getContent() instanceof Album) {
                Resources<Resource<Song>> songs = repositoryService.getSongRepository().findByAlbum((Resource<Album>) getValue());
                //songs.sort(Comparator.comparingInt(SongEntity::getTrack));
                for (Resource<Song> song : songs) {
                    getChildren().add(new ArtistTreeItem(song, repositoryService));
                }            
            }
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
	}
    
    public void reset() {
        childrenLoaded = false;
        leafPropertyComputed = false;
        getChildren();
        isLeaf();
    }
   
    public void setLeafPropertyComputed(boolean leafPropertyComputed) {
        this.leafPropertyComputed = leafPropertyComputed;
    }

    public void setChildrenLoaded(boolean childrenLoaded) {
        this.childrenLoaded = childrenLoaded;
    }
    
}
