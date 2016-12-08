
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
import ru.javafx.musicbook.client.controller.artists.ArtistsController;
import ru.javafx.musicbook.client.fxintegrity.BaseFxmlController;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;

@FXMLController(value = "/fxml/paginator/PaginatorPane.fxml")
@Scope("prototype")
public class PaginatorPaneController extends BaseFxmlController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private PagedController parentController;
    
    @FXML
    private AnchorPane paginatorPane;
    @FXML
    private ChoiceBox<Integer> sizeChoiceBox;
    @FXML
    private ComboBox<Long> pageComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {             
    }
    
    public void initPaginator(ArtistsController parentController) {
        this.parentController = parentController;
        this.parentController.setTableValue();
        initPageComboBox();
        initSizeChoiceBox();
        initListeners();
    }
    
    private void initPageComboBox() {       
        List<Long> pageNumbers = new ArrayList<>();
        for (long i = 1; i <= parentController.getPaginator().getTotalPages(); i++) {
            pageNumbers.add(i);
        }
        pageComboBox.getItems().clear();
        pageComboBox.getItems().addAll(pageNumbers);
        pageComboBox.getSelectionModel().selectFirst();
        pageComboBox.setVisibleRowCount(8);             
    }
    
    private void initSizeChoiceBox() {
        List<Integer> sizeNumbers = Arrays.asList(5, 10, 15, 20, 25);
        sizeChoiceBox.getItems().addAll(sizeNumbers);
        sizeChoiceBox.getSelectionModel().selectFirst();               
    }
    
    private void initListeners() {
        pageComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                parentController.getPaginator().setPage(newValue - 1);
                parentController.setTableValue();
            }
        });
        sizeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            parentController.getPaginator().setSize(newValue);
            parentController.setTableValue();
            initPageComboBox();
        });
    }
    
    @FXML
    private void onFirstPage() {
        parentController.getPaginator().first();
        pageComboBox.getSelectionModel().select((int) parentController.getPaginator().getPage());
    }
    
    @FXML
    private void onLastPage() {
        parentController.getPaginator().last();
        pageComboBox.getSelectionModel().select((int) parentController.getPaginator().getPage());
    }
    
    @FXML
    private void onPrevPage() {
        if (parentController.getPaginator().hasPrevious()) {
            parentController.getPaginator().previous();
            pageComboBox.getSelectionModel().select((int) parentController.getPaginator().getPage());
        } 
    }
    
    @FXML
    private void onNextPage() {
        if (parentController.getPaginator().hasNext()) {
            parentController.getPaginator().next();
            pageComboBox.getSelectionModel().select((int) parentController.getPaginator().getPage());
        }    
    }
    
    public void setParentController(ArtistsController parentController) {
        this.parentController = parentController;
    }
    
}
