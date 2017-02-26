
package ru.javafx.musicbook.client.controller.musician;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.album.AlbumPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.MusicianAlbum;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.MusicianAlbumRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_MUSICIAN_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_MUSICIAN_ALBUM;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_MUSICIAN_ALBUM;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/musician/AlbumTable.fxml")
@Scope("prototype")
public class AlbumTableController extends PagedTableController<Album> {
    
    protected MusicianPaneController paneController;
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private MusicianAlbumRepository musicianAlbumRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private ArtistRepository artistRepository;
    
    @FXML
    private TableColumn<Resource<Album>, String> artistColumn;
    @FXML
    private TableColumn<Resource<Album>, String> albumColumn;   
    @FXML
    private TableColumn<Resource<Album>, Integer> yearColumn;
    @FXML
    private TableColumn<Resource<Album>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(MusicianPaneController paneController) {
        this.paneController = paneController;
        super.initPagedTableController(albumRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() { 
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());              
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().yearProperty().asObject()); 
        artistColumn.setCellValueFactory(cellData -> { 
            try {
                return artistRepository.getResource(cellData.getValue().getLink("artist").getHref()).getContent().nameProperty();
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }           
            return new SimpleStringProperty("");
        });
    }
    
    @Override
    protected String createParamString() {
        List<String> params = new ArrayList<>();  
        params.add("musician.id=" + Helper.getId(paneController.getResource()));
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        return paramStr;
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "rating"));
    }
    
    private void initRepositoryListeners() {
        musicianAlbumRepository.clearChangeListeners(this);
        musicianRepository.clearChangeListeners(this);
        albumRepository.clearChangeListeners(this);
        
        musicianAlbumRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);
        musicianRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
        artistRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
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
                requestViewService.show(AlbumPaneController.class, selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianAlbum musicianAlbum = new MusicianAlbum();
            musicianAlbum.setMusician(paneController.getResource().getId().getHref());      
            contextMenuService.add(ADD_MUSICIAN_ALBUM, new Resource<>(musicianAlbum, new Link("null")));
            if (selectedItem != null) {
                Resource<MusicianAlbum> resMusicianAlbum = musicianAlbumRepository.findByMusicianAndAlbum(paneController.getResource(), selectedItem);
                contextMenuService.add(EDIT_MUSICIAN_ALBUM, resMusicianAlbum);
                contextMenuService.add(DELETE_MUSICIAN_ALBUM, resMusicianAlbum); 
            }
            contextMenuService.show(paneController.getView(), mouseEvent);      
        }
    }

}
