package ru.javafx.musicbook.client.controller.artist;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.ArtistReference;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.ArtistReferenceRepository;

@FXMLController(value = "/fxml/artist/ArtistReferenceTable.fxml")
@Scope("prototype")
public class ArtistReferenceTableController extends PagedTableController<ArtistReference>  {
       
    @Autowired
    private ArtistReferenceRepository artistReferenceRepository; 
    
    //@Autowired
    //private Stage primaryStage;
    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private TableColumn<Resource<ArtistReference>, String> nameReferenceColumn;
    @FXML
    private TableColumn<Resource<ArtistReference>, String> referenceColumn;   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initPagedTableController(artistReferenceRepository); 
        initRepositoryListeners();                          
    }
    
    @Override
    protected void initPagedTable() { 
        nameReferenceColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        referenceColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().referenceProperty()); 
    }
    
    @Override
    protected String createParamString() {
        List<String> params = new ArrayList<>();              
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        return paramStr;
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
    }
  
    public void initRepositoryListeners() {   
        artistReferenceRepository.clearChangeListeners(this);        
        artistReferenceRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);
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
            // если лкм выбрана запись - показать веб-страницу ссылки
            if (selectedItem != null) {
                String errorMessage = "";                
                Desktop desktop = Desktop.getDesktop();  
                if (desktop.isSupported(Desktop.Action.BROWSE)) {  
                    try {  
                         desktop.browse(new URI(selectedItem.getContent().getReference()));  
                    } catch (URISyntaxException | IOException ex) {  
                         errorMessage += "Сбой при попытке открытия ссылки.";  
                    }  
                } else {  
                    errorMessage += "На вашей платформе просмотр ссылок не из приложения не поддерживается";
                }              
                if (!errorMessage.equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    //alert.initOwner(primaryStage);
                    alert.initOwner(applicationContext.getBean("primaryStage", Stage.class));
                    alert.setTitle("ERROR");
                    alert.setContentText(errorMessage);           
                    alert.showAndWait();    
                }               
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            /*
            contextMenuService.add(ADD_ARTIST_REFERENCE, null);    
            if (selectedItem != null) {
                contextMenuService.add(EDIT_ARTIST_REFERENCE, selectedItem);
                contextMenuService.add(DELETE_ARTIST_REFERENCE, selectedItem);                           
            }
            */
            //contextMenuService.show(paneController.getView(), mouseEvent);
            contextMenuService.show(view, mouseEvent);
        }
    }
    
}
