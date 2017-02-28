package ru.javafx.musicbook.client.controller.explorer;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.controller.BaseAwareController;
import ru.javafx.musicbook.client.controller.LeftBarController;
import ru.javafx.musicbook.client.controller.album.AlbumPaneController;
import ru.javafx.musicbook.client.controller.artist.ArtistPaneController;
import ru.javafx.musicbook.client.controller.song.SongPaneController;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_ARTIST;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_ARTIST;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_ARTIST;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.SEPARATOR;
import ru.javafx.musicbook.client.service.RepositoryService;

@FXMLController("/fxml/explorer/Explorer.fxml")
public class ExplorerController extends BaseAwareController {
       
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private LeftBarController leftBarController;
    
    @FXML
    private TreeView artistTree;

    @Override
    public void initialize(URL url, ResourceBundle rb) {                
        initTreeView();
    } 
    
    /**
     * Загрузить элементы в TreeView и задать CellFactory для требуемого отображения значений элементов.
     */
    private void initTreeView() {
        // Передать корневой элемент дереву 
        artistTree.setRoot(new ArtistTreeItem(null, repositoryService));        
        // Сделать невидимым корневой элемент       
        artistTree.setShowRoot(false);      
        //добавление элементов в дерево
        fillTreeItems();
        
        artistTree.setCellFactory(new Callback<TreeView, ArtistTreeCell>() {
            @Override
            public ArtistTreeCell call(TreeView tv) {
                return new ArtistTreeCell();
            }
        });               
        // инициализировать слушателей таблиц
        new TreeViewTableListener(artistTree, repositoryService);
    }
       
    private void fillTreeItems() {      
        try {
            Resources<Resource<Artist>> artists = repositoryService.getArtistRepository().findAllNames();
            for (Resource<Artist> artist : artists) {
                TreeItem artistItem = new ArtistTreeItem(artist, repositoryService); 
                artistTree.getRoot().getChildren().add(artistItem);
            }
        } catch (URISyntaxException ex) {
            logger.info(ex.getMessage());
        }
    }

    @FXML
    private void onMouseClickTreeView(MouseEvent mouseEvent) {
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();       
        contextMenuService.clear();
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в дереве
            if (isShowingContextMenu) {
                artistTree.getSelectionModel().clearSelection();
            }
            // если лкм выбрана запись - показать её
            ArtistTreeItem selectedItem = (ArtistTreeItem) artistTree.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Resource<? extends Entity> resource = selectedItem.getValue();
                if (resource.getContent() instanceof Artist) {
                    requestViewService.show(ArtistPaneController.class, resource);
                }
                else if(resource.getContent() instanceof Album) {
                    requestViewService.show(AlbumPaneController.class, resource);
                }
                else if (resource.getContent() instanceof Song) {
                    requestViewService.show(SongPaneController.class, resource);
                }               
            }            
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showTreeContextMenu(mouseEvent);      
        }
    }

    private void showTreeContextMenu(MouseEvent mouseEvent) { 
        ArtistTreeItem selectedItem = (ArtistTreeItem) artistTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null) { 
            Resource<? extends Entity> resource = selectedItem.getValue();
            if (resource.getContent() instanceof Artist) {
                contextMenuService.add(ADD_ARTIST, null);
                // запретить удаление и редактирование записи с id = 1 (Unknown artist)
                if (!resource.getId().getHref().equals(Artist.DEFAULT_ARTIST)) {
                    contextMenuService.add(EDIT_ARTIST, resource);
                    contextMenuService.add(DELETE_ARTIST, resource);
                    contextMenuService.add(SEPARATOR);
                }
                Album newAlbum = new Album();
                newAlbum.setArtist(resource.getId().getHref());
                contextMenuService.add(ADD_ALBUM, new Resource<>(newAlbum, new Link("null")));         
            }
            else if(resource.getContent() instanceof Album) {   
                contextMenuService.add(ADD_ALBUM, null);
                // запретить удаление и редактирование записи с id = 1 (Unknown album)
                if (!resource.getId().getHref().equals(Album.DEFAULT_ALBUM)) {
                    contextMenuService.add(EDIT_ALBUM, resource);
                    contextMenuService.add(DELETE_ALBUM, resource); 
                    contextMenuService.add(SEPARATOR);
                }
                Song newSong = new Song();
                newSong.setAlbum(resource.getId().getHref());
                contextMenuService.add(ADD_SONG, new Resource<>(newSong, new Link("null")));
            } 
            else if(resource.getContent() instanceof Song) {
                contextMenuService.add(ADD_SONG, null);
                contextMenuService.add(EDIT_SONG, resource);
                contextMenuService.add(DELETE_SONG, resource);  
            }                         
        }
        //Если не выбран элемент в дереве - предоставить меню: add artist
        else {
            artistTree.getSelectionModel().clearSelection();
            contextMenuService.add(ADD_ARTIST, null);
        }
        contextMenuService.show(leftBarController.getView(), mouseEvent); 
    } 
            
}
