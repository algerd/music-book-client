
package ru.javafx.musicbook.client.controller.genres;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.BaseAwareController;
import ru.javafx.musicbook.client.controller.paginator.PagedController;
import ru.javafx.musicbook.client.controller.paginator.PaginatorPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.service.RequestService;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.*;

@FXMLController(
    value = "/fxml/genres/Genres.fxml",    
    title = "Genres")
@Scope("prototype")
public class GenresController extends BaseAwareController implements PagedController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private Resource<Genre> selectedItem;
    private PagedResources<Resource<Genre>> resources; 
    private PaginatorPaneController paginatorPaneController;
    private String searchString = "";
    
    @Autowired
    private FXMLControllerLoader fxmlLoader;  
    @Autowired
    private ArtistRepository artistRepository;   
    @Autowired
    private RequestService requestService;
    
    @FXML
    private VBox genresTableVBox;
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel;   
    //table
    @FXML
    private TableView<Resource<Genre>> genresTable;
    @FXML
    private TableColumn<Resource<Genre>, String> genreColumn;
    
    public GenresController() {}
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        initGenresTable();
    }
    
    @Override
    public void setPageValue() {
        
    }
    
    private void initGenresTable() {
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        
        genresTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = genresTable.getSelectionModel().getSelectedItem()
        );
        initPaginatorPane();
    }
    
    private void initPaginatorPane() {
        paginatorPaneController = (PaginatorPaneController) fxmlLoader.load(PaginatorPaneController.class);
        genresTableVBox.getChildren().add(paginatorPaneController.getView());
        paginatorPaneController.getPaginator().setSize(5);
        paginatorPaneController.getPaginator().setSort(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
        paginatorPaneController.initPaginator(this);
    }
    
    @FXML
    private void resetSearchLabel() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
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
                //GenreEntity genre = repositoryService.getGenreRepository().selectById(selectedItem.getId());
                //requestPageService.genrePane(genre);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_GENRE, null);
            //if (selectedItem != null && selectedItem.getId() > 1) {
                contextMenuService.add(EDIT_GENRE, selectedItem);
                contextMenuService.add(DELETE_GENRE, selectedItem);                       
            //}
            contextMenuService.show(view, mouseEvent);       
        }
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_GENRE, null);
            contextMenuService.show(view, mouseEvent);
        }      
    }
    
    private void clearSelectionTable() {
        genresTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    
}
