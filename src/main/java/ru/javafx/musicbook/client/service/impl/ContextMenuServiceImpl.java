
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
import ru.javafx.musicbook.client.controller.artist.ArtistDialogController;
import ru.javafx.musicbook.client.service.ContextMenuItemType;
import ru.javafx.musicbook.client.service.ContextMenuService;
import ru.javafx.musicbook.client.service.RequestViewService;
import static ru.javafx.musicbook.client.service.ContextMenuItemType.*;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.service.RequestService;

@Service
public class ContextMenuServiceImpl implements ContextMenuService { 

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
    private RequestViewService requestViewService;
    
    private final ContextMenu contextMenu = new ContextMenu();
    private final Map<ContextMenuItemType, EventHandler<ActionEvent>> menuMap = new HashMap<>();
    private Map<ContextMenuItemType, Resource<? extends Entity>> valueMap = new HashMap<>();
        
    public ContextMenuServiceImpl() {
        
        menuMap.put(ADD_ARTIST, e -> requestViewService.showDialog(ArtistDialogController.class, valueMap.get(ADD_ARTIST)));      
        menuMap.put(EDIT_ARTIST, e -> requestViewService.showDialog(ArtistDialogController.class, valueMap.get(EDIT_ARTIST)));          
        menuMap.put(DELETE_ARTIST, e -> requestService.deleteWithAlert(valueMap.get(DELETE_ARTIST)));      
        /*
        menuMap.put(ContextMenuItemType.ADD_ALBUM, e -> requestViewService.albumDialog(valueMap.get(ContextMenuItemType.ADD_ALBUM)));
        menuMap.put(ContextMenuItemType.EDIT_ALBUM, e -> requestViewService.albumDialog(valueMap.get(ContextMenuItemType.EDIT_ALBUM)));
        menuMap.put(ContextMenuItemType.DELETE_ALBUM, e -> deleteAlertService.show((AlbumEntity) valueMap.get(ContextMenuItemType.DELETE_ALBUM)));             
        
        menuMap.put(ContextMenuItemType.ADD_SONG, e -> requestViewService.songDialog(valueMap.get(ContextMenuItemType.ADD_SONG)));       
        menuMap.put(ContextMenuItemType.EDIT_SONG, e -> requestViewService.songDialog(valueMap.get(ContextMenuItemType.EDIT_SONG)));        
        menuMap.put(ContextMenuItemType.DELETE_SONG, e -> deleteAlertService.show((SongEntity) valueMap.get(ContextMenuItemType.DELETE_SONG)));       
        
        menuMap.put(ContextMenuItemType.ADD_GENRE, e -> requestViewService.genreDialog(valueMap.get(ContextMenuItemType.ADD_GENRE)));
        menuMap.put(ContextMenuItemType.EDIT_GENRE, e -> requestViewService.genreDialog(valueMap.get(ContextMenuItemType.EDIT_GENRE)));
        menuMap.put(ContextMenuItemType.DELETE_GENRE, e -> deleteAlertService.show((GenreEntity) valueMap.get(ContextMenuItemType.DELETE_GENRE)));     
        
        menuMap.put(ContextMenuItemType.ADD_MUSICIAN, e -> requestViewService.musicianDialog(valueMap.get(ContextMenuItemType.ADD_MUSICIAN)));
        menuMap.put(ContextMenuItemType.EDIT_MUSICIAN, e -> requestViewService.musicianDialog(valueMap.get(ContextMenuItemType.EDIT_MUSICIAN)));
        menuMap.put(ContextMenuItemType.DELETE_MUSICIAN, e -> deleteAlertService.show((MusicianEntity) valueMap.get(ContextMenuItemType.DELETE_MUSICIAN)));       
       
        menuMap.put(ContextMenuItemType.ADD_MUSICIAN_ALBUM, e -> requestViewService.musicianAlbumDialog(valueMap.get(ContextMenuItemType.ADD_MUSICIAN_ALBUM)));
        menuMap.put(ContextMenuItemType.EDIT_MUSICIAN_ALBUM, e -> requestViewService.musicianAlbumDialog(valueMap.get(ContextMenuItemType.EDIT_MUSICIAN_ALBUM)));
        menuMap.put(ContextMenuItemType.DELETE_MUSICIAN_ALBUM, e -> deleteAlertService.show((MusicianAlbumEntity) valueMap.get(ContextMenuItemType.DELETE_MUSICIAN_ALBUM))); 
        
        menuMap.put(ContextMenuItemType.ADD_MUSICIAN_GROUP, e -> requestViewService.musicianGroupDialog(valueMap.get(ContextMenuItemType.ADD_MUSICIAN_GROUP)));
        menuMap.put(ContextMenuItemType.EDIT_MUSICIAN_GROUP, e -> requestViewService.musicianGroupDialog(valueMap.get(ContextMenuItemType.EDIT_MUSICIAN_GROUP)));
        menuMap.put(ContextMenuItemType.DELETE_MUSICIAN_GROUP, e -> deleteAlertService.show((MusicianGroupEntity) valueMap.get(ContextMenuItemType.DELETE_MUSICIAN_GROUP))); 
        
        menuMap.put(ContextMenuItemType.ADD_MUSICIAN_SONG, e -> requestViewService.musicianSongDialog(valueMap.get(ContextMenuItemType.ADD_MUSICIAN_SONG)));
        menuMap.put(ContextMenuItemType.EDIT_MUSICIAN_SONG, e -> requestViewService.musicianSongDialog(valueMap.get(ContextMenuItemType.EDIT_MUSICIAN_SONG)));
        menuMap.put(ContextMenuItemType.DELETE_MUSICIAN_SONG, e -> deleteAlertService.show((MusicianSongEntity) valueMap.get(ContextMenuItemType.DELETE_MUSICIAN_SONG))); 
        
        menuMap.put(ContextMenuItemType.ADD_ARTIST_REFERENCE, e -> requestViewService.artistReferenceDialog(valueMap.get(ContextMenuItemType.ADD_ARTIST_REFERENCE)));
        menuMap.put(ContextMenuItemType.EDIT_ARTIST_REFERENCE, e -> requestViewService.artistReferenceDialog(valueMap.get(ContextMenuItemType.EDIT_ARTIST_REFERENCE)));
        menuMap.put(ContextMenuItemType.DELETE_ARTIST_REFERENCE, e -> deleteAlertService.show((ArtistReferenceEntity) valueMap.get(ContextMenuItemType.DELETE_ARTIST_REFERENCE)));
    
        menuMap.put(ContextMenuItemType.ADD_INSTRUMENT, e -> requestViewService.instrumentDialog(valueMap.get(ContextMenuItemType.ADD_INSTRUMENT)));
        menuMap.put(ContextMenuItemType.EDIT_INSTRUMENT, e -> requestViewService.instrumentDialog(valueMap.get(ContextMenuItemType.EDIT_INSTRUMENT)));
        menuMap.put(ContextMenuItemType.DELETE_INSTRUMENT, e -> deleteAlertService.show((InstrumentEntity) valueMap.get(ContextMenuItemType.DELETE_INSTRUMENT)));
        */
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
