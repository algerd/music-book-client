
package ru.javafx.musicbook.client.controller.genre;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.artist.ArtistPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_ARTIST;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_ARTIST;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_ARTIST;
import ru.javafx.musicbook.client.service.RequestViewService;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/genre/ArtistGenreTable.fxml")
@Scope("prototype")
public class ArtistGenreTableController extends PagedTableController<Artist>  {
    
    protected GenrePaneController paneController;
    
    @Autowired
    private RequestViewService requestViewService;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;
    
    @FXML
    private Label titleLabel;
    @FXML
    private TableColumn<Resource<Artist>, Integer> rankColumn;
    @FXML
    private TableColumn<Resource<Artist>, String> artistColumn;
    @FXML
    private TableColumn<Resource<Artist>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(GenrePaneController paneController) {
        this.paneController = paneController;
        titleLabel.textProperty().bind(this.paneController.getResource().getContent().nameProperty());
        super.initPagedTableController(artistRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() {       
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(pagedTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );      
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());       
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
        artistRepository.clearDeleteListeners(this);           
        artistRepository.clearUpdateListeners(this);        
        genreRepository.clearDeleteListeners(this);           
        genreRepository.clearUpdateListeners(this);
                                
        artistRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        artistRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);        
        genreRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        genreRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
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
                requestViewService.show(ArtistPaneController.class ,selectedItem);
            }           
        }      
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ARTIST, null);
            // запретить удаление и редактирование записи с id = 1 (Unknown artist)
            if (selectedItem != null && !selectedItem.getId().getHref().equals(Artist.DEFAULT_ARTIST)) {
                contextMenuService.add(EDIT_ARTIST, selectedItem);
                contextMenuService.add(DELETE_ARTIST, selectedItem);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);         
        }    
    }
     
}
