
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
import ru.javafx.musicbook.client.controller.album.AlbumPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.AlbumGenre;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumGenreRepository;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.service.ContextMenuItemType;
import ru.javafx.musicbook.client.service.RequestViewService;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/genre/AlbumGenreTable.fxml")
@Scope("prototype")
public class AlbumGenreTableController extends PagedTableController<Album>  {
    
    protected GenrePaneController paneController;
    
    @Autowired
    private RequestViewService requestViewService;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;   
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AlbumGenreRepository albumGenreRepository;
    
    @FXML
    private Label titleLabel;
    @FXML
    private TableColumn<Resource<Album>, Integer> rankColumn;
    @FXML
    private TableColumn<Resource<Album>, String> albumColumn;
    @FXML
    private TableColumn<Resource<Album>, Integer> yearColumn;
    @FXML
    private TableColumn<Resource<Album>, String> artistColumn;
    @FXML
    private TableColumn<Resource<Album>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(GenrePaneController paneController) {
        this.paneController = paneController;
        titleLabel.textProperty().bind(this.paneController.getResource().getContent().nameProperty());
        super.initPagedTableController(albumRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() {       
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(pagedTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        ); 
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().yearProperty().asObject());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());
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
        albumRepository.clearDeleteListeners(this);           
        albumRepository.clearUpdateListeners(this);        
        genreRepository.clearDeleteListeners(this);           
        genreRepository.clearUpdateListeners(this);
        albumGenreRepository.clearChangeListeners(this);
                                
        albumRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        albumRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);        
        genreRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        genreRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
        albumGenreRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);
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
            AlbumGenre albumGenre = new AlbumGenre();
            albumGenre.setGenre(paneController.getResource().getId().getHref());
            contextMenuService.add(ContextMenuItemType.ADD_GENRE_ALBUM, new Resource<>(albumGenre, new Link("null")));
            if (selectedItem != null) {
                Resource<AlbumGenre> resAlbumGenre = albumGenreRepository.findByAlbumAndGenre(selectedItem, (Resource<Genre>) paneController.getResource());
                contextMenuService.add(ContextMenuItemType.EDIT_GENRE_ALBUM, resAlbumGenre);
                contextMenuService.add(ContextMenuItemType.DELETE_GENRE_ALBUM, resAlbumGenre);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);         
        }    
    }
     
}
