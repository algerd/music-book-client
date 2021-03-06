
package ru.javafx.musicbook.client.controller.explorer;

import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.datacore.Entity;
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
                List<Resource<Album>> albums = repositoryService.getAlbumRepository().findByArtist((Resource<Artist>) getValue()).getContent()
                        .parallelStream()
                        .sorted(Comparator.comparingInt(res -> res.getContent().getYear()))
                        //.sorted((e1, e2) -> Integer.compare(e1.getContent().getYear(), e2.getContent().getYear()))
                        .collect(Collectors.toList());                             
                for (Resource<Album> album : albums) {
                    getChildren().add(new ArtistTreeItem(album, repositoryService));            
                }
            }
            else if (getValue().getContent() instanceof Album) {
                List<Resource<Song>> songs = repositoryService.getSongRepository().findByAlbum((Resource<Album>) getValue()).getContent()
                        .parallelStream()
                        .sorted(Comparator.comparingInt(res -> res.getContent().getTrack()))
                        //.sorted((e1, e2) -> Integer.compare(e1.getContent().getTrack(), e2.getContent().getTrack()))
                        .collect(Collectors.toList());
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
