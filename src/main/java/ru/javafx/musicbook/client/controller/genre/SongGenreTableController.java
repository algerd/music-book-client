
package ru.javafx.musicbook.client.controller.genre;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
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
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.entity.SongGenre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.SongGenreRepository;
import ru.javafx.musicbook.client.repository.SongRepository;
import ru.javafx.musicbook.client.service.ContextMenuItemType;
import ru.javafx.musicbook.client.service.RequestViewService;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/genre/SongGenreTable.fxml")
@Scope("prototype")
public class SongGenreTableController extends PagedTableController<Song>  {
    
    protected GenrePaneController paneController;
    
    @Autowired
    private RequestViewService requestViewService;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository; 
    @Autowired
    private SongRepository songRepository;     
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private SongGenreRepository songGenreRepository;
    
    @FXML
    private Label titleLabel;
    @FXML
    private TableColumn<Resource<Song>, Integer> rankColumn;    
    @FXML
    private TableColumn<Resource<Song>, String> songColumn;
    @FXML
    private TableColumn<Resource<Song>, String> albumColumn;
    @FXML
    private TableColumn<Resource<Song>, Integer> yearColumn;
    @FXML
    private TableColumn<Resource<Song>, String> artistColumn;
    @FXML
    private TableColumn<Resource<Song>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(GenrePaneController paneController) {
        this.paneController = paneController;
        titleLabel.textProperty().bind(this.paneController.getResource().getContent().nameProperty());
        super.initPagedTableController(songRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() {       
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(pagedTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        ); 
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
            return new SimpleIntegerProperty(0).asObject();        
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
        params.add("genre.id=" + Helper.getId(paneController.getResource()));
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        return paramStr;
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
    }
    
    private void initRepositoryListeners() {                         
        songRepository.clearDeleteListeners(this);           
        songRepository.clearUpdateListeners(this);        
        genreRepository.clearDeleteListeners(this);           
        genreRepository.clearUpdateListeners(this);
        songGenreRepository.clearChangeListeners(this);
                                
        albumRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        albumRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);        
        genreRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        genreRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
        songGenreRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);
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
            SongGenre songGenre = new SongGenre();
            songGenre.setGenre(paneController.getResource().getId().getHref());
            contextMenuService.add(ContextMenuItemType.ADD_GENRE_SONG, new Resource<>(songGenre, new Link("null")));
            if (selectedItem != null) {
                Resource<SongGenre> resSongGenre = songGenreRepository.findBySongAndGenre(selectedItem, (Resource<Genre>) paneController.getResource());
                contextMenuService.add(ContextMenuItemType.EDIT_GENRE_SONG, resSongGenre);
                contextMenuService.add(ContextMenuItemType.DELETE_GENRE_SONG, resSongGenre);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);         
        }    
    }
     
}
