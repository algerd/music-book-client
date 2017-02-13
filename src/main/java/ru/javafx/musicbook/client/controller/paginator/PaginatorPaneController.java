
package ru.javafx.musicbook.client.controller.paginator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.artists.ArtistsController;
import ru.javafx.musicbook.client.fxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;

@FXMLController(value = "/fxml/paginator/PaginatorPane.fxml")
@Scope("prototype")
public class PaginatorPaneController extends BaseFxmlController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final int DEFAULT_PAGE_SIZE = 10;
    
    private PagedTableController parentController;
    private Paginator paginator;
    private boolean initPageComboBox = false;
    
    @FXML
    private AnchorPane paginatorPane;
    @FXML
    private ChoiceBox<Integer> sizeChoiceBox;
    @FXML
    private ComboBox<Integer> pageComboBox;
    
    public PaginatorPaneController() {
        paginator = new Paginator(DEFAULT_PAGE_SIZE);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {             
    }
    
    public void initPaginator(PagedTableController parentController) {
        this.parentController = parentController;
        this.parentController.setPageValue();
        initPageComboBox();
        initSizeChoiceBox();
        initListeners();
    }
        
    public void initPageComboBox() {       
        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 1; i <= paginator.getTotalPages(); i++) {
            pageNumbers.add(i);
        }
        pageComboBox.getItems().clear();
        pageComboBox.getItems().addAll(pageNumbers); 
        initPageComboBox = true;
        pageComboBox.getSelectionModel().selectFirst();
        pageComboBox.setVisibleRowCount(8);          
    }
    
    public void initSizeChoiceBox() {
        List<Integer> sizeNumbers = Arrays.asList(5, 10, 15, 20, 25);
        sizeChoiceBox.getItems().clear();
        sizeChoiceBox.getItems().addAll(sizeNumbers);
        sizeChoiceBox.getSelectionModel().selectFirst();        
    }
    
    private void initListeners() {
        pageComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                paginator.setPage(newValue - 1);
                if (!(initPageComboBox && pageComboBox.getSelectionModel().selectedItemProperty().getValue() == 1)) {           
                    parentController.setPageValue();   
                } 
                initPageComboBox = false;
            }
        });
        sizeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                paginator.setSize(newValue);
                parentController.setPageValue();
                initPageComboBox();               
            }
        });
    }
    
    @FXML
    private void onFirstPage() {
        paginator.first();
        pageComboBox.getSelectionModel().select(paginator.getPage());
    }
    
    @FXML
    private void onLastPage() {
        paginator.last();
        pageComboBox.getSelectionModel().select(paginator.getPage());
    }
    
    @FXML
    private void onPrevPage() {
        if (paginator.hasPrevious()) {
            paginator.previous();
            pageComboBox.getSelectionModel().select(paginator.getPage());
        } 
    }
    
    @FXML
    private void onNextPage() {
        if (paginator.hasNext()) {
            paginator.next();
            pageComboBox.getSelectionModel().select(paginator.getPage());
        }    
    }
    
    public void setParentController(ArtistsController parentController) {
        this.parentController = parentController;
    }

    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }
    
}
