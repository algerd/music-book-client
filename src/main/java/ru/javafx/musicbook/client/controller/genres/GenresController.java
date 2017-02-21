
package ru.javafx.musicbook.client.controller.genres;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.controller.PagedTableController;
import ru.javafx.musicbook.client.controller.genre.GenrePaneController;
import ru.javafx.musicbook.client.controller.paginator.Sort;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.fxintegrity.FXMLController;
import ru.javafx.musicbook.client.repository.AlbumGenreRepository;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistGenreRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.MusicianGenreRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.SongGenreRepository;
import ru.javafx.musicbook.client.repository.SongRepository;
import ru.javafx.musicbook.client.repository.operators.StringOperator;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.*;
import ru.javafx.musicbook.client.service.RequestViewService;

@FXMLController(
    value = "/fxml/genres/Genres.fxml",    
    title = "Genres")
@Scope("prototype")
public class GenresController extends PagedTableController<Genre> {

    private String searchString = "";
     
    @Autowired
    private RequestViewService requestViewService;
    @Autowired
    private GenreRepository genreRepository;   
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private MusicianRepository musicianRepository;
    @Autowired
    private ArtistGenreRepository artistGenreRepository;
    @Autowired
    private AlbumGenreRepository albumGenreRepository;
    @Autowired
    private SongGenreRepository  songGenreRepository;
    @Autowired
    private MusicianGenreRepository musicianGenreRepository;
    
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel;   
    //table
    @FXML
    private TableColumn<Resource<Genre>, String> genreColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> artistsAmountColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> artistsAvRatingColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> albumsAmountColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> albumsAvRatingColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> songsAmountColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> songsAvRatingColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> musiciansAmountColumn;
    @FXML
    private TableColumn<Resource<Genre>, Resource<Genre>> musiciansAvRatingColumn;
      
    public GenresController() {}
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        initFilters();
        super.setPagedTableHeaderSize(2);
        super.initPagedTableController(genreRepository);
        initRepositoryListeners();
        initFilterListeners();
    }
       
    @Override
    protected String createParamString() {
        List<String> params = new ArrayList<>();              
        if (!searchString.equals("")) {
            params.add("name=" + StringOperator.STARTS_WITH_IGNORE_CASE);
            params.add("name=" + searchString);
        }
        params.addAll(paginatorPaneController.getPaginator().getParameterList());
        String paramStr = params.isEmpty()? "" : String.join("&", params);
        logger.info("paramStr :{}", paramStr);
        return paramStr;
    }
    
    @Override
    protected void initPagedTable() {
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().getContent().nameProperty());       
        
        artistsAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        artistsAmountColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {  
                            this.setText("" + artistGenreRepository.countArtistsByGenre(item));
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });       
        artistsAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        artistsAvRatingColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) { 
                        try {                                             
                            List<Artist> artists = new ArrayList<>();
                            artistRepository.findByGenre(item).getContent().parallelStream().forEach(
                                artistResource -> artists.add(artistResource.getContent())
                            );        
                            int averageRating = 0;
                            for (Artist artist : artists) {
                                averageRating += artist.getRating();
                            }
                            int count = artists.size();
                            this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / count + 0.5))/ 100.0 : " - "));                            
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
        
        albumsAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        albumsAmountColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {                   
                            this.setText("" + albumGenreRepository.countAlbumsByGenre(item));
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
        albumsAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        albumsAvRatingColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {                                             
                            List<Album> albums = new ArrayList<>();
                            albumRepository.findByGenre(item).getContent().parallelStream().forEach(
                                albumResource -> albums.add(albumResource.getContent())
                            );        
                            int averageRating = 0;
                            for (Album album : albums) {
                                averageRating += album.getRating();
                            }
                            int count = albums.size();
                            this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / count + 0.5))/ 100.0 : " - "));                            
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
        
        songsAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        songsAmountColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {                   
                            this.setText("" + songGenreRepository.countSongsByGenre(item));
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
        songsAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        songsAvRatingColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {                                             
                            List<Song> songs = new ArrayList<>();
                            songRepository.findByGenre(item).getContent().parallelStream().forEach(
                                songResource -> songs.add(songResource.getContent())
                            );        
                            int averageRating = 0;
                            for (Song song : songs) {
                                averageRating += song.getRating();
                            }
                            int count = songs.size();
                            this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / count + 0.5))/ 100.0 : " - "));                            
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
        
        musiciansAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        musiciansAmountColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {                   
                            this.setText("" + musicianGenreRepository.countMusiciansByGenre(item));
                        } catch (URISyntaxException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            };
			return cell;
        });
        musiciansAvRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        musiciansAvRatingColumn.setCellFactory(col -> {
            TableCell<Resource<Genre>, Resource<Genre>> cell = new TableCell<Resource<Genre>, Resource<Genre>>() {
                @Override
                public void updateItem(Resource<Genre> item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        try {                                             
                            List<Musician> musicians = new ArrayList<>();
                            musicianRepository.findByGenre(item).getContent().parallelStream().forEach(
                                songResource -> musicians.add(songResource.getContent())
                            );        
                            int averageRating = 0;
                            for (Musician musician : musicians) {
                                averageRating += musician.getRating();
                            }
                            int count = musicians.size();
                            this.setText("" + ((count != 0) ? ((int) (100.0 * averageRating / count + 0.5))/ 100.0 : " - "));                            
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
    protected Sort getSort() {
        return new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
    }
   
    private void initFilterListeners() {        
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {           
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();
            filter();   
        });
    }
    
    private void initFilters() {
        resetSearchLabel.setVisible(false);
    }
    
    private void filter() {       
        setPageValue();
        paginatorPaneController.initPageComboBox();
    }
    
    @FXML
    private void resetSearchLabel() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
    }
    
    private void initRepositoryListeners() {                    
       //repositoryService.getAlbumGenreRepository().clearChangeListeners(this);                          
       //repositoryService.getSongGenreRepository().clearChangeListeners(this);                       
       //repositoryService.getMusicianGenreRepository().clearChangeListeners(this);                             
       //repositoryService.getMusicianRepository().clearDeleteListeners(this);
       musicianRepository.clearChangeListeners(this);
       songRepository.clearChangeListeners(this);
       albumRepository.clearChangeListeners(this);
       artistRepository.clearChangeListeners(this);
       genreRepository.clearChangeListeners(this);     
                             
        //repositoryService.getAlbumGenreRepository().addChangeListener(this::changed, this);                          
        //repositoryService.getSongGenreRepository().addChangeListener(this::changed, this);                       
        //repositoryService.getMusicianGenreRepository().addChangeListener(this::changed, this);                            
        //repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);
        musicianRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        songRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        albumRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        artistRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);
        genreRepository.addChangeListener((observable, oldVal, newVal) -> filter(), this);           
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
                requestViewService.show(GenrePaneController.class, selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_GENRE, null);
            if (selectedItem != null && !selectedItem.getId().getHref().equals(Genre.DEFAULT_GENRE)) {
                contextMenuService.add(EDIT_GENRE, selectedItem);
                contextMenuService.add(DELETE_GENRE, selectedItem);                       
            }
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
  
}
