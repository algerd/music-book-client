package ru.javafx.musicbook.client.controller.artist;

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
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_ALBUM;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/artist/AlbumTable.fxml")
@Scope("prototype")
public class AlbumTableController extends PagedTableController<Album> {
    
    protected ArtistPaneController paneController;
    
    @Autowired
    private AlbumRepository albumRepository; 
      
    @FXML
    private TableColumn<Resource<Album>, String> albumNameColumn;
    @FXML
    private TableColumn<Resource<Album>, Integer> albumYearColumn;
    @FXML
    private TableColumn<Resource<Album>, String> albumTimeColumn;
    @FXML
    private TableColumn<Resource<Album>, Integer> albumRatingColumn;  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(ArtistPaneController paneController) {
        this.paneController = paneController;
        super.initPagedTableController(albumRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() { 
        albumNameColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        albumYearColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().yearProperty().asObject());
        albumTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().timeProperty());
        albumRatingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());                
    }
    
    @Override
    protected String createParamString() {
        List<String> params = new ArrayList<>();  
        params.add("artist.id=" + Helper.getId(paneController.getResource()));
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        return paramStr;
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "year"));
    }
    
    void initRepositoryListeners() {
        albumRepository.clearChangeListeners(this);
        albumRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);
    }

    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) { 
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();       
        contextMenuService.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                //requestPageService.albumPane(selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            Album newAlbum = new Album();
            newAlbum.setArtist(paneController.getResource().getId().getHref());
            contextMenuService.add(ADD_ALBUM, new Resource<>(newAlbum, new Link("null")));
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (selectedItem != null && !selectedItem.getId().getHref().equals(Album.DEFAULT_ALBUM)) {
                contextMenuService.add(EDIT_ALBUM, selectedItem);
                contextMenuService.add(DELETE_ALBUM, selectedItem);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent); 
        }
    }
      
}
