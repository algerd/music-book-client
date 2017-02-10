
package ru.javafx.musicbook.client.controller;

import java.net.URISyntaxException;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.paginator.PagedController;
import ru.javafx.musicbook.client.controller.paginator.PaginatorPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;
import ru.javafx.musicbook.client.repository.CrudRepository;
import ru.javafx.musicbook.client.utils.Helper;

public abstract class PagedTableController<T extends Entity> extends BaseAwareController implements PagedController {
    
    protected Resource<T> selectedItem;
    protected PagedResources<Resource<T>> resources; 
    protected PaginatorPaneController paginatorPaneController;
    protected CrudRepository<T> pagedTableRepository;
    
    @Autowired
    protected FXMLControllerLoader fxmlLoader;
    
    @FXML
    protected TableView<Resource<T>> artistsTable;
    @FXML
    protected Pane tableContainer;
    
    protected abstract String createParamString();
    protected abstract void initPagedTable();
    protected abstract Sort getSort();
    
    public void initPagedTableController(CrudRepository<T> pagedTableRepository) {
        this.pagedTableRepository = pagedTableRepository;
        initPagedTable(); 
        artistsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = artistsTable.getSelectionModel().getSelectedItem()
        );
        initPaginatorPane();
    }
    
    private void initPaginatorPane() {
        paginatorPaneController = (PaginatorPaneController) fxmlLoader.load(PaginatorPaneController.class);
        tableContainer.getChildren().add(paginatorPaneController.getView());
        paginatorPaneController.getPaginator().setSize(5);    
        paginatorPaneController.getPaginator().setSort(getSort());
        paginatorPaneController.initPaginator(this);
    }
    
    @Override
    public void setPageValue() {      
        clearSelectionTable();
        artistsTable.getItems().clear();                      
        try {     
            resources = pagedTableRepository.getPagedResources(createParamString());  
            //logger.info("Paged table resources: {}", resources);
            paginatorPaneController.getPaginator().setTotalElements((int) resources.getMetadata().getTotalElements());           
            artistsTable.setItems(FXCollections.observableArrayList(resources.getContent().parallelStream().collect(Collectors.toList())));           
            Helper.setHeightTable(artistsTable, paginatorPaneController.getPaginator().getSize());        
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
    } 
           
    private void clearSelectionTable() {
        artistsTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }

}
