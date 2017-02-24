
package ru.javafx.musicbook.client.controller.musician;

import ru.javafx.musicbook.client.controller.artist.*;
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
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.MusicianGroup;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.MusicianGroupRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_MUSICIAN_GROUP;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_MUSICIAN_GROUP;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_MUSICIAN_GROUP;
import ru.javafx.musicbook.client.service.RequestViewService;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/musician/ArtistTable.fxml")
@Scope("prototype")
public class ArtistTableController extends PagedTableController<Artist> {
    
    protected MusicianPaneController paneController;
    
    @Autowired
    private RequestViewService requestViewService;
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private MusicianGroupRepository musicianGroupRepository;
    @Autowired
    private ArtistRepository artistRepository;
    
    @FXML
    private TableColumn<Resource<Artist>, String> artistColumn;
    @FXML
    private TableColumn<Resource<Artist>, String> startDateColumn;
    @FXML
    private TableColumn<Resource<Artist>, String> endDateColumn;
    @FXML
    private TableColumn<Resource<Artist>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(MusicianPaneController paneController) {
        this.paneController = paneController;
        super.initPagedTableController(artistRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() { 
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());       
        startDateColumn.setCellValueFactory(cellData -> {
             try {
                return musicianGroupRepository.findByMusicianAndArtist(paneController.getResource(), cellData.getValue()).getContent().startDateProperty();
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }
            return new SimpleStringProperty("");            
        });
        endDateColumn.setCellValueFactory(cellData -> {
             try {
                return musicianGroupRepository.findByMusicianAndArtist(paneController.getResource(), cellData.getValue()).getContent().endDateProperty();
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
        musicianGroupRepository.clearChangeListeners(this);
        musicianRepository.clearChangeListeners(this);
        artistRepository.clearChangeListeners(this);
        
        musicianGroupRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);
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
                requestViewService.show(ArtistPaneController.class, selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianGroup musicianGroup = new MusicianGroup();
            musicianGroup.setMusician(paneController.getResource().getId().getHref());      
            contextMenuService.add(ADD_MUSICIAN_GROUP, new Resource<>(musicianGroup, new Link("null")));
            if (selectedItem != null) {
                Resource<MusicianGroup> resMusicianGroup = musicianGroupRepository.findByArtist(selectedItem);
                contextMenuService.add(EDIT_MUSICIAN_GROUP, resMusicianGroup);
                contextMenuService.add(DELETE_MUSICIAN_GROUP, resMusicianGroup);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);      
        }
    }

}
