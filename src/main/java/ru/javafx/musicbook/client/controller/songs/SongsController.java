
package ru.javafx.musicbook.client.controller.songs;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.Params;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.controller.song.SongPaneController;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.SongRepository;
import ru.javafx.musicbook.client.repository.operators.StringOperator;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.ADD_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.DELETE_SONG;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.EDIT_SONG;
import ru.javafx.musicbook.client.service.RequestViewService;
import ru.javafx.musicbook.client.utils.Helper;

@FXMLController(
    value = "/fxml/songs/Songs.fxml",    
    title = "Songs")
@Scope("prototype")
public class SongsController extends PagedTableController<Song> {

    private Resource<Genre> resorceGenre;
    private String searchString = ""; 
    private SongsController.SearchSelector searchSelector;
    private String sort;
    private String order;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
    
    @Autowired
    private RequestViewService requestViewService;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private SongRepository songRepository;    
    @Autowired
    private GenreRepository genreRepository;
    // filters:
    @FXML
    private ChoiceBox<Resource<Genre>> genreChoiceBox;
    @FXML
    private Spinner<Integer> minRatingSpinner; 
    @FXML
    private Spinner<Integer> maxRatingSpinner;    
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel;   
    @FXML
    private ChoiceBox<SongsController.SearchSelector> searchChoiceBox;
    @FXML
    private ChoiceBox<String> sortChoiceBox;
    @FXML
    private ChoiceBox<String> orderChoiceBox;
    // table
    @FXML
    private TableColumn<Resource<Song>, Integer> rankColumn;
    @FXML
    private TableColumn<Resource<Song>, Resource<Song>> artistColumn;
    @FXML
    private TableColumn<Resource<Song>, Resource<Song>> albumColumn;
    @FXML
    private TableColumn<Resource<Song>, String> songColumn;    
    @FXML
    private TableColumn<Resource<Song>, Resource<Song>> yearColumn;
    @FXML
    private TableColumn<Resource<Song>, Integer> ratingColumn;
    
    private enum SearchSelector {
        ARTIST("Artist"), ALBUM("Album"), SONG("Song");
        private final String name;
        private SearchSelector(String name) {
            this.name = name;
        } 
        @Override
        public String toString() {
            return name;
        }        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sort = "Rating";
        order = "Asc";
        searchSelector = SongsController.SearchSelector.SONG;
        initSortAndOrderChoiceBoxes();
        initGenreChoiceBox();
        initSearchChoiceBox();
        initFilters();
        super.initPagedTableController(songRepository); 
        initRepositoryListeners();
        initFilterListeners();      
    }
    
    @Override
    protected void initPagedTable() {
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(pagedTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        songColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().ratingProperty().asObject()); 
        
        albumColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        albumColumn.setCellFactory(col -> {
			TableCell<Resource<Song>, Resource<Song>> cell = new TableCell<Resource<Song>, Resource<Song>>() {
                @Override
                public void updateItem(Resource<Song> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                       
                        try {  
                            this.setText(albumRepository.getResource(item.getLink("album").getHref()).getContent().getName());
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }                                          
                    }
                }
            };
			return cell;
		});
       
        yearColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        yearColumn.setCellFactory(col -> {
			TableCell<Resource<Song>, Resource<Song>> cell = new TableCell<Resource<Song>, Resource<Song>>() {
                @Override
                public void updateItem(Resource<Song> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                       
                        try {  
                            this.setText("" + albumRepository.getResource(item.getLink("album").getHref()).getContent().getYear());
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }                                          
                    }
                }
            };
			return cell;
		});
               
        artistColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        artistColumn.setCellFactory(col -> {
			TableCell<Resource<Song>, Resource<Song>> cell = new TableCell<Resource<Song>, Resource<Song>>() {
                @Override
                public void updateItem(Resource<Song> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                    
                        try {  
                            this.setText(artistRepository.getPagedResources("song.id=" + Helper.getId(item)).getContent().iterator().next().getContent().getName());
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
        if (getMinRating() != Params.MIN_RATING || getMaxRating() != Params.MAX_RATING) {
            params.add("rating=" + getMinRating());
            params.add("rating=" + getMaxRating());
        }                   
        if (!searchString.equals("")) {
            String name = "";
            switch (searchSelector) {
                case SONG:
                    name = "name=";
                    break;
                case ALBUM:
                    name = "album.name=";
                    break;
                case ARTIST:
                    name = "artist.name=";
                    break;
            }
            params.add(name + StringOperator.STARTS_WITH);
            params.add(name + searchString);
        }
        if (!resorceGenre.getContent().getName().equals("All genres")) {
            params.add("genre.id=" + Helper.getId(resorceGenre));
        }
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        logger.info("paramStr :{}", paramStr);
        return paramStr;
    }
    
    private void initSortAndOrderChoiceBoxes() {
        sortChoiceBox.getItems().addAll("Rating", "Name");
        sortChoiceBox.getSelectionModel().selectFirst();
        orderChoiceBox.getItems().addAll("Asc", "Desc");
        orderChoiceBox.getSelectionModel().selectFirst();   
    }
    
    private void initGenreChoiceBox() {
        genreChoiceBox.getItems().clear();
        Helper.initResourceChoiceBox(genreChoiceBox); 
           
        Genre emptyGenre = new Genre();
        emptyGenre.setName("All genres");
        resorceGenre = new Resource<>(emptyGenre, new Link(Genre.DEFAULT_GENRE));
        
        List <Resource<Genre>> genreResources = new ArrayList<>();
        genreResources.add(resorceGenre);
        try {
            genreResources.addAll(genreRepository.findAllNames().getContent().parallelStream().collect(Collectors.toList()));         
            genreChoiceBox.getItems().addAll(genreResources);
            genreChoiceBox.getSelectionModel().selectFirst();
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    private void initSearchChoiceBox() {
        searchChoiceBox.getItems().addAll(Arrays.asList(SearchSelector.values()));       
        searchChoiceBox.getSelectionModel().select(SearchSelector.SONG);
    }
    
    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating);                      
        resetSearchLabel.setVisible(false);
    }
    
    private void initFilterListeners() {
        minRating.addListener((ObservableValue, oldValue, newValue)-> filter());
        maxRating.addListener((ObservableValue, oldValue, newValue)-> filter());
        
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();
            filter();   
        }); 
        genreChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                resorceGenre = newValue;                            
                filter();
            }
        });
        sortChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            sort = newValue;
            filter();
        });
        orderChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            order = newValue;
            filter();
        });        
        searchChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            searchSelector = newValue;
            filter();
        });      
    }
    
    private void initRepositoryListeners() {    
        artistRepository.clearChangeListeners(this);
        albumRepository.clearChangeListeners(this);
        songRepository.clearChangeListeners(this);
        genreRepository.clearChangeListeners(this);
        
        artistRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        albumRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        songRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        genreRepository.addChangeListener(this::changedGenre, this);
    }
    
    private void changedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        initGenreChoiceBox();
        resetFilter();
    }
    
    private void filter() {
        paginatorPaneController.getPaginator().setSort(getSort());
        setPageValue();
        paginatorPaneController.initPageComboBox();
    }
   
    @FXML
    private void resetSearchField() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
    }
    
    @FXML
    private void resetFilter() {
        resetSearchField();
        sortChoiceBox.getSelectionModel().selectFirst();
        orderChoiceBox.getSelectionModel().selectFirst();
        genreChoiceBox.getSelectionModel().selectFirst();  
        searchChoiceBox.getSelectionModel().select(SongsController.SearchSelector.SONG);
        initFilters();
        paginatorPaneController.initPageComboBox();
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
                requestViewService.show(SongPaneController.class, selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_SONG, null);
            contextMenuService.add(EDIT_SONG, selectedItem);
            contextMenuService.add(DELETE_SONG, selectedItem);  
            contextMenuService.show(view, mouseEvent);  
        }
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            contextMenuService.add(ADD_SONG, null);
            contextMenuService.show(view, mouseEvent);
        } 
    }
    
    @Override
    protected Sort getSort() {
        return new Sort(new Sort.Order(
           order.equals("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
           sort.toLowerCase()
        ));
    }
    
    public int getMaxRating() {
        return maxRating.get();
    }
    public void setMaxRating(int value) {
        maxRating.set(value);
    }
    public IntegerProperty maxRatingProperty() {
        return maxRating;
    }
    
    public int getMinRating() {
        return minRating.get();
    }
    public void setMinRating(int value) {
        minRating.set(value);
    }
    public IntegerProperty minRatingProperty() {
        return minRating;
    }

}
