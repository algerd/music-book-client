
package ru.javafx.musicbook.client.controller.musician;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
import ru.javafx.musicbook.client.entity.MusicianSong;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.MusicianSongRepository;
import ru.javafx.musicbook.client.repository.SongRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_MUSICIAN_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_MUSICIAN_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_MUSICIAN_SONG;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/musician/SongTable.fxml")
@Scope("prototype")
public class SongTableController extends PagedTableController<Song> {
    
    protected MusicianPaneController paneController;
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private MusicianSongRepository musicianSongRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private ArtistRepository artistRepository;
    
    @FXML
    private Label titleLabel;
    @FXML
    private TableColumn<Resource<Song>, String> artistColumn;
    @FXML
    private TableColumn<Resource<Song>, String> albumColumn;   
    @FXML
    private TableColumn<Resource<Song>, Integer> yearColumn;
    @FXML
    private TableColumn<Resource<Song>, String> songColumn;       
    @FXML
    private TableColumn<Resource<Song>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(MusicianPaneController paneController) {
        this.paneController = paneController;
        super.initPagedTableController(songRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() { 
        songColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());              
        albumColumn.setCellValueFactory(cellData -> {
            try {
                return albumRepository.getResource(cellData.getValue().getLink("album").getHref()).getContent().nameProperty();
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }           
            return new SimpleStringProperty("");
        });
        yearColumn.setCellValueFactory(cellData -> {
            try {
                return albumRepository.getResource(cellData.getValue().getLink("album").getHref()).getContent().yearProperty().asObject();
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }           
            return new SimpleObjectProperty<>();
        });
        artistColumn.setCellValueFactory(cellData -> {
            try {
                return artistRepository.getPagedResources("song.id=" + Helper.getId(cellData.getValue())).getContent().iterator().next().getContent().nameProperty();
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
        musicianSongRepository.clearChangeListeners(this);
        musicianRepository.clearChangeListeners(this);
        songRepository.clearChangeListeners(this);
        
        musicianSongRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);
        musicianRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
        songRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
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
                requestViewService.show(SongPaneController.class, selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianSong musicianSong = new MusicianSong();
            musicianSong.setMusician(paneController.getResource().getId().getHref());
            contextMenuService.add(ADD_MUSICIAN_SONG, new Resource<>(musicianSong, new Link("null")));
            if (selectedItem != null) {
                Resource<MusicianSong> resMusicianSong = musicianSongRepository.findByMusicianAndSong(paneController.getResource(), selectedItem);
                contextMenuService.add(EDIT_MUSICIAN_SONG, resMusicianSong);
                contextMenuService.add(DELETE_MUSICIAN_SONG, resMusicianSong); 
            }
            contextMenuService.show(paneController.getView(), mouseEvent);        
        }
    }

}
