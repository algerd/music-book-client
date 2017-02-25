
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
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGenre;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianGenreRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_GENRE_MUSICIAN;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_GENRE_MUSICIAN;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_GENRE_MUSICIAN;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/genre/MusicianGenreTable.fxml")
@Scope("prototype")
public class MusicianGenreTableController extends PagedTableController<Musician>  {
    
    protected GenrePaneController paneController;
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MusicianGenreRepository musicianGenreRepository;
    @Autowired
    private InstrumentRepository instrumentRepository; 
    
    @FXML
    private Label titleLabel;
    @FXML
    private TableColumn<Resource<Musician>, Integer> rankColumn;
    @FXML
    private TableColumn<Resource<Musician>, String> musicianColumn;
    @FXML
    private TableColumn<Resource<Musician>, String> instrumentColumn;   
    @FXML
    private TableColumn<Resource<Musician>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(GenrePaneController paneController) {
        this.paneController = paneController;
        titleLabel.textProperty().bind(this.paneController.getResource().getContent().nameProperty());
        super.initPagedTableController(musicianRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() {       
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(pagedTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );      
        musicianColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());       
        
        instrumentColumn.setCellValueFactory(cellData -> {    
            List<Instrument> instruments = new ArrayList<>();
            try {
                instrumentRepository.findByMusician(cellData.getValue()).getContent().parallelStream().forEach(
                    resource -> instruments.add(resource.getContent())
                );
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }
            String str = "";
            for (Instrument instrument : instruments){
                str += (!str.equals("")) ? ", " : "";
                str += instrument.getName();
            }                          
            return new SimpleStringProperty(str);
        });
        
        /*
        instrumentColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        instrumentColumn.setCellFactory(col -> {
            TableCell<Resource<Musician>, Resource<Musician>> cell = new TableCell<Resource<Musician>, Resource<Musician>>() {
                @Override
                public void updateItem(Resource<Musician> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                               
                        try {     
                            List<Instrument> instruments = new ArrayList<>();
                            instrumentRepository.findByMusician(item).getContent().parallelStream().forEach(
                                resource -> instruments.add(resource.getContent())
                            );
                            String str = "";
                            for (Instrument instrument :  instruments){
                                str += (!str.equals("")) ? ", " : "";
                                str += instrument.getName();
                            }
                            this.setText(str);                           
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }        
                    }
                }
            };
			return cell;
        });
        */
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
        musicianRepository.clearDeleteListeners(this);           
        musicianRepository.clearUpdateListeners(this);          
        genreRepository.clearDeleteListeners(this);           
        genreRepository.clearUpdateListeners(this);      
        musicianGenreRepository.clearChangeListeners(this);
        
        musicianRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        musicianRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);          
        genreRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);           
        genreRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);      
        musicianGenreRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);   
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
                //requestViewService.show(MusicianPaneController.class ,selectedItem);
            }           
        }      
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianGenre musicianGenre = new MusicianGenre();
            musicianGenre.setGenre(paneController.getResource().getId().getHref());
            contextMenuService.add(ADD_GENRE_MUSICIAN, new Resource<>(musicianGenre, new Link("null")));
            if (selectedItem != null) {              
                Resource<MusicianGenre> resMusicianGenre = musicianGenreRepository.findByMusicianAndGenre(selectedItem, (Resource<Genre>) paneController.getResource());
                contextMenuService.add(EDIT_GENRE_MUSICIAN, resMusicianGenre);
                contextMenuService.add(DELETE_GENRE_MUSICIAN, resMusicianGenre);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);
        }    
    }
     
}
