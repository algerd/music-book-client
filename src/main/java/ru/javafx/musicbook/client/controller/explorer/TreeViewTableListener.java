
package ru.javafx.musicbook.client.controller.explorer;

import java.net.URISyntaxException;
import java.util.Comparator;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.datacore.entity.Entity;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.datacore.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.service.RepositoryService;

public class TreeViewTableListener {
    
    private final RepositoryService repositoryService;
    private final ArtistTreeItem rootTreeItem;
    private final TreeView artistTree;
    
    public TreeViewTableListener(TreeView artistTree, RepositoryService repositoryService) {
        this.artistTree = artistTree;
        this.rootTreeItem = (ArtistTreeItem) artistTree.getRoot();
        this.repositoryService = repositoryService;
        init();    
    }
    
    private void init() {
        //remove
        repositoryService.getArtistRepository().clearDeleteListeners(this);       
        repositoryService.getAlbumRepository().clearDeleteListeners(this);
        repositoryService.getSongRepository().clearDeleteListeners(this);
              
        repositoryService.getArtistRepository().clearInsertListeners(this);
        repositoryService.getAlbumRepository().clearInsertListeners(this);
        repositoryService.getSongRepository().clearInsertListeners(this); 
   
        repositoryService.getArtistRepository().clearUpdateListeners(this);
        repositoryService.getAlbumRepository().clearUpdateListeners(this);
        repositoryService.getSongRepository().clearUpdateListeners(this);
        
        //add
        repositoryService.getArtistRepository().addDeleteListener(this::deleted, this);       
        repositoryService.getAlbumRepository().addDeleteListener(this::deleted, this);
        repositoryService.getSongRepository().addDeleteListener(this::deleted, this);
              
        repositoryService.getArtistRepository().addInsertListener(this::addedArtist, this);
        repositoryService.getAlbumRepository().addInsertListener(this::addedAlbum, this);
        repositoryService.getSongRepository().addInsertListener(this::addedSong, this); 
   
        repositoryService.getArtistRepository().addUpdateListener(this::updatedArtistName, this);
        repositoryService.getAlbumRepository().addUpdateListener(this::updatedAlbum, this);
        repositoryService.getSongRepository().addUpdateListener(this::updatedSong, this);
    }
    
    private void addedArtist(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {        
        Resource<? extends Entity> newResource = ((WrapChangedEntity<Resource<? extends Entity>>) newValue).getNew(); 
        if (newResource != null && newResource.getContent() instanceof Artist) {
            artistTree.getSelectionModel().clearSelection();
            ArtistTreeItem artistItem = new ArtistTreeItem(newResource, repositoryService);
            rootTreeItem.getChildren().add(artistItem);
            rootTreeItem.getChildren().sort(Comparator.comparing(res -> ((Resource<Artist>) res.getValue()).getContent().getName()));
        }  
    }
    
    private void updatedArtistName(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {     
        Resource<Artist> oldResource = ((WrapChangedEntity<Resource<Artist>>) newVal).getOld();
        Resource<Artist> newResource = ((WrapChangedEntity<Resource<Artist>>) newVal).getNew();       
        if (!oldResource.getContent().getName().equals(newResource.getContent().getName())) {
            rootTreeItem.getChildren().sort(Comparator.comparing(res -> ((Resource<Artist>) res.getValue()).getContent().getName()));
        }       
    }
    
    private void addedAlbum(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) { 
        Resource<? extends Entity> newResource = ((WrapChangedEntity<Resource<? extends Entity>>) newValue).getNew(); 
        if (newResource != null && newResource.getContent() instanceof Album) {
            try {
                artistTree.getSelectionModel().clearSelection();
                Resource<Artist> parentEntity = repositoryService.getArtistRepository().getResource(newResource.getLink("artist").getHref());
                searchTreeItem(rootTreeItem, parentEntity).reset();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }    
    }
    
    private void updatedAlbum(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
        deleted(observable, oldVal, newVal);
        addedAlbum(observable, oldVal, newVal);      
    }
    
    private void addedSong(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {      
        Resource<? extends Entity> newResource = ((WrapChangedEntity<Resource<? extends Entity>>) newValue).getNew(); 
        if (newResource != null && newResource.getContent() instanceof Song) {
            try {
                artistTree.getSelectionModel().clearSelection();
                Resource<Album> parentEntity = repositoryService.getAlbumRepository().getResource(newResource.getLink("album").getHref());
                searchTreeItem(rootTreeItem, parentEntity).reset();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }    
    }
    
    private void updatedSong(ObservableValue<? extends Object> observable, Object oldVal, Object newVal) {
        deleted(observable, oldVal, newVal);
        addedSong(observable, oldVal, newVal);      
    }
          
    //TODO: после удаления записи надо вызывать isLeaf для случаев, когда все записи у родителя удаляются, а значок не удаляется
    private void deleted(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
        Resource<? extends Entity> oldResource = ((WrapChangedEntity<Resource<? extends Entity>>) newValue).getOld();             
        if (oldResource != null) {
            artistTree.getSelectionModel().clearSelection();        
            ArtistTreeItem removedItem = searchTreeItem(rootTreeItem, oldResource);
            if (removedItem != null) {
                ArtistTreeItem parentRemovedItem = (ArtistTreeItem) removedItem.getParent();
                parentRemovedItem.getChildren().remove(removedItem);
                parentRemovedItem.setLeafPropertyComputed(false);
                parentRemovedItem.isLeaf();
                if (parentRemovedItem.getChildren().isEmpty() && parentRemovedItem.isExpanded()) {
                    parentRemovedItem.setExpanded(false);
                }
            } 
        }    
    }
       
    /**
     * Рекурсивно найти TreeItem сущности searchEntity из дерева rootItem.
     */
    private ArtistTreeItem searchTreeItem(ArtistTreeItem rootItem, Resource<? extends Entity> searchEntity) {
        ArtistTreeItem treeItem = null;
        for (TreeItem<Resource<? extends Entity>> objectItem : rootItem.getChildren()) {
            ArtistTreeItem childItem = (ArtistTreeItem) objectItem;
            Resource<? extends Entity> childEntity = childItem.getValue();
            if (childEntity.getId().equals(searchEntity.getId())) {  
                treeItem = childItem;             
                break;
            }
            if (!childItem.isLeaf()) {
                ArtistTreeItem deepChildItem = searchTreeItem(childItem, searchEntity);
                if (deepChildItem != null) {
                    treeItem = deepChildItem;
                    break;
                }               
            }           
        }
        return treeItem;     
    }
    
}
