
package ru.javafx.musicbook.client.controller.song;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.musician.MusicianPaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianSong;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.MusicianSongRepository;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_MUSICIAN_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_MUSICIAN_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_MUSICIAN_SONG;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(value = "/fxml/song/MusicianTable.fxml")
@Scope("prototype")
public class MusicianTableController extends PagedTableController<Musician> {
    
    protected SongPaneController paneController;
    
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private MusicianSongRepository musicianSongRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;
    
    @FXML
    private TableColumn<Resource<Musician>, String> musicianColumn;
    @FXML
    private TableColumn<Resource<Musician>, Resource<Musician>> instrumentColumn;   
    @FXML
    private TableColumn<Resource<Musician>, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {            
    }
    
    public void bootstrap(SongPaneController paneController) {
        this.paneController = paneController;
        super.initPagedTableController(musicianRepository); 
        initRepositoryListeners();
    }
    
    @Override
    protected void initPagedTable() { 
        musicianColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());       
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject());
        
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
    }
    
    @Override
    protected String createParamString() {
        List<String> params = new ArrayList<>();  
        params.add("song.id=" + Helper.getId(paneController.getResource()));
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        return paramStr;
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
    }
    
    private void initRepositoryListeners() {
        musicianSongRepository.clearChangeListeners(this);
        musicianRepository.clearChangeListeners(this);
        instrumentRepository.clearChangeListeners(this);
        
        musicianSongRepository.addChangeListener((observable, oldVal, newVal) -> setPageValue(), this);
        musicianRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
        instrumentRepository.addUpdateListener((observable, oldVal, newVal) -> setPageValue(), this);
        instrumentRepository.addDeleteListener((observable, oldVal, newVal) -> setPageValue(), this);
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
                requestViewService.show(MusicianPaneController.class ,selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianSong musicianSong = new MusicianSong();
            musicianSong.setSong(paneController.getResource().getId().getHref());      
            contextMenuService.add(ADD_MUSICIAN_SONG, new Resource<>(musicianSong, new Link("null")));
            if (selectedItem != null) {
                Resource<MusicianSong> resMusicianSong = musicianSongRepository.findByMusicianAndSong(selectedItem, paneController.getResource());
                contextMenuService.add(EDIT_MUSICIAN_SONG, resMusicianSong);
                contextMenuService.add(DELETE_MUSICIAN_SONG, resMusicianSong);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);      
        }
    }

}
