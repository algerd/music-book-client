
package ru.javafx.musicbook.client.service.impl;

import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import ru.javafx.musicbook.client.controller.album.AlbumDialogController;
import ru.javafx.musicbook.client.controller.artist.ArtistDialogController;
import ru.javafx.musicbook.client.controller.artist.ArtistReferenceDialogController;
import ru.javafx.musicbook.client.controller.genre.AlbumGenreDialogController;
import ru.javafx.musicbook.client.controller.genre.ArtistGenreDialogController;
import ru.javafx.musicbook.client.controller.genre.GenreDialogController;
import ru.javafx.musicbook.client.controller.genre.MusicianGenreDialogController;
import ru.javafx.musicbook.client.controller.genre.SongGenreDialogController;
import ru.javafx.musicbook.client.controller.instrument.InstrumentDialogController;
import ru.javafx.musicbook.client.controller.instrument.MusicianInstrumentDialogController;
import ru.javafx.musicbook.client.controller.musician.MusicianAlbumDialogController;
import ru.javafx.musicbook.client.controller.musician.MusicianDialogController;
import ru.javafx.musicbook.client.controller.musician.MusicianGroupDialogController;
import ru.javafx.musicbook.client.controller.musician.MusicianSongDialogController;
import ru.javafx.musicbook.client.controller.song.SongDialogController;
import ru.javafx.musicbook.client.service.ContextMenuItemType;
import ru.javafx.musicbook.client.service.ContextMenuService;
import ru.javafx.musicbook.client.service.RequestViewService;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.*;
import ru.javafx.musicbook.client.datacore.entity.Entity;
import ru.javafx.musicbook.client.repository.AlbumGenreRepository;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistGenreRepository;
import ru.javafx.musicbook.client.repository.ArtistReferenceRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianAlbumRepository;
import ru.javafx.musicbook.client.repository.MusicianGenreRepository;
import ru.javafx.musicbook.client.repository.MusicianGroupRepository;
import ru.javafx.musicbook.client.repository.MusicianInstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
import ru.javafx.musicbook.client.repository.MusicianSongRepository;
import ru.javafx.musicbook.client.repository.SongGenreRepository;
import ru.javafx.musicbook.client.repository.SongRepository;

@Service
public class ContextMenuServiceImpl implements ContextMenuService { 

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private RequestViewService requestViewService;   
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;    
    @Autowired
    private AlbumRepository albumRepository; 
    @Autowired
    private SongRepository songRepository; 
    @Autowired
    private MusicianRepository musicianRepository; 
    @Autowired
    private InstrumentRepository instrumentRepository;  
    @Autowired
    private ArtistReferenceRepository artistReferenceRepository;
    @Autowired
    private MusicianGroupRepository musicianGroupRepository;
    @Autowired
    private MusicianAlbumRepository musicianAlbumRepository;
    @Autowired
    private MusicianSongRepository musicianSongRepository;
    @Autowired
    private ArtistGenreRepository artistGenreRepository;
    @Autowired
    private AlbumGenreRepository albumGenreRepository;
    @Autowired
    private SongGenreRepository songGenreRepository;
    @Autowired
    private MusicianGenreRepository musicianGenreRepository;
    @Autowired
    private MusicianInstrumentRepository musicianInstrumentRepository;
         
    private final ContextMenu contextMenu = new ContextMenu();
    private final Map<ContextMenuItemType, EventHandler<ActionEvent>> menuMap = new HashMap<>();
    private Map<ContextMenuItemType, Resource<? extends Entity>> valueMap = new HashMap<>();
        
    public ContextMenuServiceImpl() {
        
        menuMap.put(ADD_ARTIST, e -> requestViewService.showDialog(ArtistDialogController.class, valueMap.get(ADD_ARTIST)));      
        menuMap.put(EDIT_ARTIST, e -> requestViewService.showDialog(ArtistDialogController.class, valueMap.get(EDIT_ARTIST)));          
        menuMap.put(DELETE_ARTIST, e -> artistRepository.deleteWithAlert(valueMap.get(DELETE_ARTIST)));      
        
        menuMap.put(ADD_GENRE, e -> requestViewService.showDialog(GenreDialogController.class, valueMap.get(ADD_GENRE)));
        menuMap.put(EDIT_GENRE, e -> requestViewService.showDialog(GenreDialogController.class, valueMap.get(EDIT_GENRE)));
        menuMap.put(DELETE_GENRE, e -> genreRepository.deleteWithAlert(valueMap.get(DELETE_GENRE)));      
     
        menuMap.put(ADD_ALBUM, e -> requestViewService.showDialog(AlbumDialogController.class, valueMap.get(ADD_ALBUM)));
        menuMap.put(EDIT_ALBUM, e -> requestViewService.showDialog(AlbumDialogController.class, valueMap.get(EDIT_ALBUM)));
        menuMap.put(DELETE_ALBUM, e -> albumRepository.deleteWithAlert(valueMap.get(DELETE_ALBUM)));   
             
        menuMap.put(ADD_SONG, e -> requestViewService.showDialog(SongDialogController.class, valueMap.get(ADD_SONG)));       
        menuMap.put(EDIT_SONG, e -> requestViewService.showDialog(SongDialogController.class, valueMap.get(EDIT_SONG)));        
        menuMap.put(DELETE_SONG, e -> songRepository.deleteWithAlert(valueMap.get(DELETE_SONG)));       
                       
        menuMap.put(ADD_MUSICIAN, e -> requestViewService.showDialog(MusicianDialogController.class, valueMap.get(ADD_MUSICIAN))); 
        menuMap.put(EDIT_MUSICIAN, e -> requestViewService.showDialog(MusicianDialogController.class, valueMap.get(EDIT_MUSICIAN)));
        menuMap.put(DELETE_MUSICIAN, e -> musicianRepository.deleteWithAlert(valueMap.get(DELETE_MUSICIAN)));       
       
        menuMap.put(ADD_MUSICIAN_ALBUM, e -> requestViewService.showDialog(MusicianAlbumDialogController.class, valueMap.get(ADD_MUSICIAN_ALBUM)));
        menuMap.put(EDIT_MUSICIAN_ALBUM, e -> requestViewService.showDialog(MusicianAlbumDialogController.class, valueMap.get(EDIT_MUSICIAN_ALBUM)));
        menuMap.put(DELETE_MUSICIAN_ALBUM, e -> musicianAlbumRepository.deleteWithAlert(valueMap.get(DELETE_MUSICIAN_ALBUM)));
       
        menuMap.put(ADD_MUSICIAN_SONG, e -> requestViewService.showDialog(MusicianSongDialogController.class, valueMap.get(ADD_MUSICIAN_SONG)));
        menuMap.put(EDIT_MUSICIAN_SONG, e -> requestViewService.showDialog(MusicianSongDialogController.class, valueMap.get(EDIT_MUSICIAN_SONG)));
        menuMap.put(DELETE_MUSICIAN_SONG, e -> musicianSongRepository.deleteWithAlert(valueMap.get(DELETE_MUSICIAN_SONG)));
       
        menuMap.put(ADD_MUSICIAN_GROUP, e -> requestViewService.showDialog(MusicianGroupDialogController.class, valueMap.get(ADD_MUSICIAN_GROUP)));
        menuMap.put(EDIT_MUSICIAN_GROUP, e -> requestViewService.showDialog(MusicianGroupDialogController.class, valueMap.get(EDIT_MUSICIAN_GROUP)));
        menuMap.put(DELETE_MUSICIAN_GROUP, e -> musicianGroupRepository.deleteWithAlert(valueMap.get(DELETE_MUSICIAN_GROUP))); 
        
        menuMap.put(ADD_ARTIST_REFERENCE, e -> requestViewService.showDialog(ArtistReferenceDialogController.class, valueMap.get(ADD_ARTIST_REFERENCE)));
        menuMap.put(EDIT_ARTIST_REFERENCE, e -> requestViewService.showDialog(ArtistReferenceDialogController.class, valueMap.get(EDIT_ARTIST_REFERENCE)));
        menuMap.put(DELETE_ARTIST_REFERENCE, e -> artistReferenceRepository.deleteWithAlert(valueMap.get(DELETE_ARTIST_REFERENCE))); 
        
        menuMap.put(ADD_INSTRUMENT, e -> requestViewService.showDialog(InstrumentDialogController.class, valueMap.get(ADD_INSTRUMENT)));
        menuMap.put(EDIT_INSTRUMENT, e -> requestViewService.showDialog(InstrumentDialogController.class, valueMap.get(EDIT_INSTRUMENT)));
        menuMap.put(DELETE_INSTRUMENT, e -> instrumentRepository.deleteWithAlert(valueMap.get(DELETE_INSTRUMENT)));
        
        menuMap.put(ADD_GENRE_ARTIST, e -> requestViewService.showDialog(ArtistGenreDialogController.class, valueMap.get(ADD_GENRE_ARTIST)));
        menuMap.put(EDIT_GENRE_ARTIST, e -> requestViewService.showDialog(ArtistGenreDialogController.class, valueMap.get(EDIT_GENRE_ARTIST)));
        menuMap.put(DELETE_GENRE_ARTIST, e -> artistGenreRepository.deleteWithAlert(valueMap.get(DELETE_GENRE_ARTIST)));
        
        menuMap.put(ADD_GENRE_ALBUM, e -> requestViewService.showDialog(AlbumGenreDialogController.class, valueMap.get(ADD_GENRE_ALBUM)));
        menuMap.put(EDIT_GENRE_ALBUM, e -> requestViewService.showDialog(AlbumGenreDialogController.class, valueMap.get(EDIT_GENRE_ALBUM)));
        menuMap.put(DELETE_GENRE_ALBUM, e -> albumGenreRepository.deleteWithAlert(valueMap.get(DELETE_GENRE_ALBUM)));
        
        menuMap.put(ADD_GENRE_SONG, e -> requestViewService.showDialog(SongGenreDialogController.class, valueMap.get(ADD_GENRE_SONG)));
        menuMap.put(EDIT_GENRE_SONG, e -> requestViewService.showDialog(SongGenreDialogController.class, valueMap.get(EDIT_GENRE_SONG)));
        menuMap.put(DELETE_GENRE_SONG, e -> songGenreRepository.deleteWithAlert(valueMap.get(DELETE_GENRE_SONG)));
    
        menuMap.put(ADD_GENRE_MUSICIAN, e -> requestViewService.showDialog(MusicianGenreDialogController.class, valueMap.get(ADD_GENRE_MUSICIAN)));
        menuMap.put(EDIT_GENRE_MUSICIAN, e -> requestViewService.showDialog(MusicianGenreDialogController.class, valueMap.get(EDIT_GENRE_MUSICIAN)));
        menuMap.put(DELETE_GENRE_MUSICIAN, e -> musicianGenreRepository.deleteWithAlert(valueMap.get(DELETE_GENRE_MUSICIAN)));
    
        menuMap.put(ADD_INSTRUMENT_MUSICIAN, e -> requestViewService.showDialog(MusicianInstrumentDialogController.class, valueMap.get(ADD_INSTRUMENT_MUSICIAN)));
        menuMap.put(EDIT_INSTRUMENT_MUSICIAN, e -> requestViewService.showDialog(MusicianInstrumentDialogController.class, valueMap.get(EDIT_INSTRUMENT_MUSICIAN)));
        menuMap.put(DELETE_INSTRUMENT_MUSICIAN, e -> musicianInstrumentRepository.deleteWithAlert(valueMap.get(DELETE_INSTRUMENT_MUSICIAN)));
    }
    
    @Override
    public void add(ContextMenuItemType itemType, Resource<? extends Entity> resource) {  
        // сохранить в карте переменных resource для элемента меню itemType
        valueMap.put(itemType, resource);
        // получить элемент меню
        MenuItem item = itemType.get();   
        // задать экшен элементу меню
        item.setOnAction(menuMap.get(itemType));
        // добавить в меню элемент
        contextMenu.getItems().add(item);   
    }
    
    @Override
    public void add(ContextMenuItemType itemType) {
        contextMenu.getItems().add(itemType.get());
    }
        
    @Override
    public void show(Parent parent, MouseEvent mouseEvent) {
        contextMenu.show(parent, mouseEvent.getScreenX(), mouseEvent.getScreenY());
    }
      
    @Override
    public void clear() {
        contextMenu.hide();
        contextMenu.getItems().clear();
        valueMap.clear();
    }
    
    @Override
    public ContextMenu getContextMenu() {
        return contextMenu;
    }
    
}
