
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
import ru.javafx.musicbook.client.controller.paginator.PaginatorPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.datacore.entity.Entity;
import ru.javafx.musicbook.client.fxintegrity.FXMLControllerLoader;
import ru.javafx.musicbook.client.datacore.repository.CrudRepository;
import ru.javafx.musicbook.client.utils.Helper;

public abstract class PagedTableController<T extends Entity> extends BaseAwareController {
    
    protected Resource<T> selectedItem;
    protected PagedResources<Resource<T>> resources; 
    protected PaginatorPaneController paginatorPaneController;
    protected CrudRepository<T> pagedTableRepository;
    protected int pagedTableSize = 5;
    protected int pagedTableHeaderSize = 1;
    
    @Autowired
    protected FXMLControllerLoader fxmlLoader;
    
    @FXML
    protected TableView<Resource<T>> pagedTable;
    @FXML
    protected Pane tableContainer;
    
    protected abstract String createParamString();
    protected abstract void initPagedTable();
    protected abstract Sort getSort();
    
    public void initPagedTableController(CrudRepository<T> pagedTableRepository) {
        this.pagedTableRepository = pagedTableRepository;
        initPagedTable(); 
        pagedTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectedItem = pagedTable.getSelectionModel().getSelectedItem());
        initPaginatorPane();
    }
    
    private void initPaginatorPane() {
        paginatorPaneController = (PaginatorPaneController) fxmlLoader.load(PaginatorPaneController.class);
        tableContainer.getChildren().add(paginatorPaneController.getView());
        paginatorPaneController.getPaginator().setSize(pagedTableSize);    
        paginatorPaneController.getPaginator().setSort(getSort());
        paginatorPaneController.initPaginator(this);
    }
    
    public void setPageValue() {      
        clearSelectionTable();
        pagedTable.getItems().clear();                      
        try {     
            resources = pagedTableRepository.getPagedResources(createParamString());  
            //logger.info("Paged table resources: {}", resources);
            paginatorPaneController.getPaginator().setTotalElements((int) resources.getMetadata().getTotalElements());           
            pagedTable.setItems(FXCollections.observableArrayList(resources.getContent().parallelStream().collect(Collectors.toList())));           
            Helper.setHeightTable(pagedTable, paginatorPaneController.getPaginator().getSize(), pagedTableHeaderSize);        
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
    } 
           
    public void clearSelectionTable() {
        pagedTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }

    public CrudRepository<T> getPagedTableRepository() {
        return pagedTableRepository;
    }

    public void setPagedTableRepository(CrudRepository<T> pagedTableRepository) {
        this.pagedTableRepository = pagedTableRepository;
    }

    public int getPagedTableSize() {
        return pagedTableSize;
    }

    public void setPagedTableSize(int pagedTableSize) {
        this.pagedTableSize = pagedTableSize;
    }

    public int getPagedTableHeaderSize() {
        return pagedTableHeaderSize;
    }

    public void setPagedTableHeaderSize(int pagedTableHeaderSize) {
        this.pagedTableHeaderSize = pagedTableHeaderSize;
    }

}
