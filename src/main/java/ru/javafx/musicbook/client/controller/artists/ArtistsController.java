
package ru.javafx.musicbook.client.controller.artists;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseAwareController;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.*;
import ru.javafx.musicbook.client.service.RequestService;
import ru.javafx.musicbook.client.utils.Helper;
import ru.javafx.musicbook.client.utils.PageRequest;
import ru.javafx.musicbook.client.utils.Sort;
import ru.javafx.musicbook.client.utils.Sort.Direction;

@FXMLController(
    value = "/fxml/artists/Artists.fxml",    
    title = "Artists")
@Scope("prototype")
public class ArtistsController extends BaseAwareController {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Resource<Artist> selectedItem;
    private PagedResources<Resource<Artist>> resources;
    private PageRequest pageRequest;
    
    public ArtistsController() {}
    
    @Autowired
    private ArtistRepository artistRepository;
    
    @Autowired
    private RequestService requestService;
       
    //table
    @FXML
    private TableView<Resource<Artist>> artistsTable;
    @FXML
    private TableColumn<Resource<Artist>, Integer> rankColumn;
    @FXML
    private TableColumn<Resource<Artist>, String> artistColumn;
    @FXML
    private TableColumn<Resource<Artist>, Integer> ratingColumn; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        initArtistsTable();
        pageRequest = new PageRequest(0, 5, new Sort(new Sort.Order(Direction.ASC, "name")));
        setTableValue();
    } 
    
    private void initArtistsTable() { 
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(artistsTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );      
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());       
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject()); 
        
        // Добавить слушателя на выбор элемента в таблице.
        artistsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = artistsTable.getSelectionModel().getSelectedItem()
        );
    }
    
    public void setTableValue() {
        requestGetArtists();
        artistsTable.setItems(FXCollections.observableArrayList(resources.getContent().stream().collect(Collectors.toList())));      
        //sort();       
        Helper.setHeightTable(artistsTable, 10);  
    }    
    
    @FXML
    private void onPrevPage() {
        if (resources.getPreviousLink() != null) {
            pageRequest = (PageRequest) pageRequest.previous();
            clearSelectionTable();
            artistsTable.getItems().clear();
            setTableValue();
        } 
    }
    
    @FXML
    private void onNextPage() {
        if (resources.getNextLink() != null) {
            pageRequest = (PageRequest) pageRequest.next();
            clearSelectionTable();
            artistsTable.getItems().clear();
            setTableValue();
        }    
    }
    
    private void requestGetArtists() {    
        try { 
            //resources = artistRepository.getArtists();  
            //resources = artistRepository.getArtists(0, 5, null); 
            /*
            PageRequest pageRequest = new PageRequest(0, 5, new Sort(
                    new Sort.Order(Direction.ASC, "name"),
                    new Sort.Order(Direction.ASC, "rating")           
            ));
            */
            resources = artistRepository.getArtists(pageRequest);
            
            PagedResources.PageMetadata metadata = resources.getMetadata();
            //logger.info("Got {} of {} artists: ", resources.getContent().size(), metadata.getTotalElements());                                
            /*                
            resources.getContent().parallelStream().forEach(
                resource -> {
                    logger.info("content: {}", resource.getContent()); 
                    logger.info("links: {}", resource.getLinks());
                }
            );
            */
        } 
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    /**
     * ЛКМ - зызов окна выбранного альбома selectedAlbum;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного selectedAlbum или нового альбома.
     */
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
                //requestPageService.artistPane(selectedItem);
            }           
        }      
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ARTIST, null);
            // запретить удаление и редактирование записи с id = 1 (Unknown artist)
            //if (selectedItem != null && selectedItem.getId() != 1) {
                contextMenuService.add(EDIT_ARTIST, selectedItem);
                contextMenuService.add(DELETE_ARTIST, selectedItem);                       
            //}
            contextMenuService.show(view, mouseEvent);         
        }    
    }
    
    /**
     * При ПКМ по странице артиста показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ARTIST, null);
            contextMenuService.show(view, mouseEvent);
        }      
    }
    
    private void clearSelectionTable() {
        artistsTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }

}
