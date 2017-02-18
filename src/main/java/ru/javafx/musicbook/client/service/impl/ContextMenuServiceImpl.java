
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
import ru.javafx.musicbook.client.controller.genre.GenreDialogController;
import ru.javafx.musicbook.client.controller.instrument.InstrumentDialogController;
import ru.javafx.musicbook.client.controller.musician.MusicianAlbumDialogController;
import ru.javafx.musicbook.client.controller.musician.MusicianDialogController;
import ru.javafx.musicbook.client.controller.musician.MusicianGroupDialogController;
import ru.javafx.musicbook.client.controller.song.SongDialogController;
import ru.javafx.musicbook.client.service.ContextMenuItemType;
import ru.javafx.musicbook.client.service.ContextMenuService;
import ru.javafx.musicbook.client.service.RequestViewService;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.*;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistReferenceRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.GenreRepository;
import ru.javafx.musicbook.client.repository.InstrumentRepository;
import ru.javafx.musicbook.client.repository.MusicianGroupRepository;
import ru.javafx.musicbook.client.repository.MusicianRepository;
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
    private MusicianGroupRepository musicianAlbumRepository;
    
      
    private final ContextMenu contextMenu = new ContextMenu();
    private final Map<ContextMenuItemType, EventHandler<ActionEvent>> menuMap = new HashMap<>();
    private Map<ContextMenuItemType, Resource<? extends Entity>> valueMap = new HashMap<>();
        
    public ContextMenuServiceImpl() {
        
        menuMap.put(ADD_ARTIST, e -> requestViewService.showDialog(ArtistDialogController.class, valueMap.get(ADD_ARTIST)));      
        menuMap.put(EDIT_ARTIST, e -> requestViewService.showDialog(ArtistDialogController.class, valueMap.get(EDIT_ARTIST)));          
        menuMap.put(DELETE_ARTIST, e -> artistRepository.deleteWithAlert(valueMap.get(DELETE_ARTIST)));      
        
        menuMap.put(ContextMenuItemType.ADD_GENRE, e -> requestViewService.showDialog(GenreDialogController.class, valueMap.get(ADD_GENRE)));
        menuMap.put(ContextMenuItemType.EDIT_GENRE, e -> requestViewService.showDialog(GenreDialogController.class, valueMap.get(EDIT_GENRE)));
        menuMap.put(ContextMenuItemType.DELETE_GENRE, e -> genreRepository.deleteWithAlert(valueMap.get(DELETE_GENRE)));      
     
        menuMap.put(ContextMenuItemType.ADD_ALBUM, e -> requestViewService.showDialog(AlbumDialogController.class, valueMap.get(ADD_ALBUM)));
        menuMap.put(ContextMenuItemType.EDIT_ALBUM, e -> requestViewService.showDialog(AlbumDialogController.class, valueMap.get(EDIT_ALBUM)));
        menuMap.put(ContextMenuItemType.DELETE_ALBUM, e -> albumRepository.deleteWithAlert(valueMap.get(DELETE_ALBUM)));   
       
        
        menuMap.put(ContextMenuItemType.ADD_SONG, e -> requestViewService.showDialog(SongDialogController.class, valueMap.get(ADD_SONG)));       
        menuMap.put(ContextMenuItemType.EDIT_SONG, e -> requestViewService.showDialog(SongDialogController.class, valueMap.get(EDIT_SONG)));        
        menuMap.put(ContextMenuItemType.DELETE_SONG, e -> songRepository.deleteWithAlert(valueMap.get(DELETE_SONG)));       
                       
        menuMap.put(ContextMenuItemType.ADD_MUSICIAN, e -> requestViewService.showDialog(MusicianDialogController.class, valueMap.get(ADD_MUSICIAN))); 
        menuMap.put(ContextMenuItemType.EDIT_MUSICIAN, e -> requestViewService.showDialog(MusicianDialogController.class, valueMap.get(EDIT_MUSICIAN)));
        menuMap.put(ContextMenuItemType.DELETE_MUSICIAN, e -> musicianRepository.deleteWithAlert(valueMap.get(DELETE_MUSICIAN)));       
       
        menuMap.put(ContextMenuItemType.ADD_MUSICIAN_ALBUM, e -> requestViewService.showDialog(MusicianAlbumDialogController.class, valueMap.get(ADD_MUSICIAN_ALBUM)));
        menuMap.put(ContextMenuItemType.EDIT_MUSICIAN_ALBUM, e -> requestViewService.showDialog(MusicianAlbumDialogController.class, valueMap.get(EDIT_MUSICIAN_ALBUM)));
        menuMap.put(ContextMenuItemType.DELETE_MUSICIAN_ALBUM, e -> musicianAlbumRepository.deleteWithAlert(valueMap.get(DELETE_MUSICIAN_ALBUM)));
        /*
        menuMap.put(ContextMenuItemType.ADD_MUSICIAN_SONG, e -> requestViewService.musicianSongDialog(valueMap.get(ContextMenuItemType.ADD_MUSICIAN_SONG)));
        menuMap.put(ContextMenuItemType.EDIT_MUSICIAN_SONG, e -> requestViewService.musicianSongDialog(valueMap.get(ContextMenuItemType.EDIT_MUSICIAN_SONG)));
        menuMap.put(ContextMenuItemType.DELETE_MUSICIAN_SONG, e -> deleteAlertService.show((MusicianSongEntity) valueMap.get(ContextMenuItemType.DELETE_MUSICIAN_SONG))); 
        */
        menuMap.put(ContextMenuItemType.ADD_MUSICIAN_GROUP, e -> requestViewService.showDialog(MusicianGroupDialogController.class, valueMap.get(ADD_MUSICIAN_GROUP)));
        menuMap.put(ContextMenuItemType.EDIT_MUSICIAN_GROUP, e -> requestViewService.showDialog(MusicianGroupDialogController.class, valueMap.get(EDIT_MUSICIAN_GROUP)));
        menuMap.put(ContextMenuItemType.DELETE_MUSICIAN_GROUP, e -> musicianGroupRepository.deleteWithAlert(valueMap.get(DELETE_MUSICIAN_GROUP))); 
        
        menuMap.put(ContextMenuItemType.ADD_ARTIST_REFERENCE, e -> requestViewService.showDialog(ArtistReferenceDialogController.class, valueMap.get(ADD_ARTIST_REFERENCE)));
        menuMap.put(ContextMenuItemType.EDIT_ARTIST_REFERENCE, e -> requestViewService.showDialog(ArtistReferenceDialogController.class, valueMap.get(EDIT_ARTIST_REFERENCE)));
        menuMap.put(ContextMenuItemType.DELETE_ARTIST_REFERENCE, e -> artistReferenceRepository.deleteWithAlert(valueMap.get(DELETE_ARTIST_REFERENCE))); 
        
        menuMap.put(ContextMenuItemType.ADD_INSTRUMENT, e -> requestViewService.showDialog(InstrumentDialogController.class, valueMap.get(ADD_INSTRUMENT)));
        menuMap.put(ContextMenuItemType.EDIT_INSTRUMENT, e -> requestViewService.showDialog(InstrumentDialogController.class, valueMap.get(EDIT_INSTRUMENT)));
        menuMap.put(ContextMenuItemType.DELETE_INSTRUMENT, e -> instrumentRepository.deleteWithAlert(valueMap.get(DELETE_INSTRUMENT)));
        
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
