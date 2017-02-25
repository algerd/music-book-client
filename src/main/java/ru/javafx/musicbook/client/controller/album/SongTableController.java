
package ru.javafx.musicbook.client.controller.album;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.controller.song.SongPaneController;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.SongRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_SONG;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/album/SongTable.fxml")
@Scope("prototype")
public class SongTableController extends PagedTableController<Song> {
    
    protected AlbumPaneController paneController;
    
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private ArtistRepository artistRepository;
    
    @FXML
    private TableColumn<Resource<Song>, String> songNameColumn;
    @FXML
    private TableColumn<Resource<Song>, Integer> songTrackColumn;
    @FXML
    private TableColumn<Resource<Song>, String> songTimeColumn;
    @FXML
    private TableColumn<Resource<Song>, Integer> songRatingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(AlbumPaneController paneController) {
        this.paneController = paneController;
        super.initPagedTableController(songRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() { 
        songNameColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        songTrackColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().trackProperty().asObject());
        songTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().timeProperty());
        songRatingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());
    }
    
    @Override
    protected String createParamString() {
        List<String> params = new ArrayList<>();  
        params.add("album.id=" + Helper.getId(paneController.getResource()));
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        return paramStr;
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "track"));
    }
    
    private void initRepositoryListeners() { 
        songRepository.clearChangeListeners(this);
        albumRepository.clearChangeListeners(this);
        artistRepository.clearChangeListeners(this);

        songRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);
        albumRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
        albumRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        artistRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
        artistRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);            
    }
        
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) throws URISyntaxException { 
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();       
        contextMenuService.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                requestViewService.show(SongPaneController.class ,selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            Song newSong = new Song();
            newSong.setAlbum(paneController.getResource().getId().getHref());
            contextMenuService.add(ADD_SONG, new Resource<>(newSong, new Link("null")));
            contextMenuService.add(EDIT_SONG, selectedItem);
            contextMenuService.add(DELETE_SONG, selectedItem);   
            contextMenuService.show(paneController.getView(), mouseEvent);
        }
    }    

}
