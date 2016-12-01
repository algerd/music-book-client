
package ru.javafx.musicbook.client.service;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public enum ContextMenuItemType {

        ADD_ARTIST(new MenuItem("Add Artist")),
        EDIT_ARTIST(new MenuItem("Edit Artist")),
        DELETE_ARTIST(new MenuItem("Delete Artist"));
        /*
        ADD_ALBUM(new MenuItem("Add Album")),
        EDIT_ALBUM(new MenuItem("Edit Album")),
        DELETE_ALBUM(new MenuItem("Delete Album")),
        
        ADD_SONG(new MenuItem("Add Song")),
        EDIT_SONG(new MenuItem("Edit Song")),
        DELETE_SONG(new MenuItem("Delete Song")),
        
        ADD_GENRE(new MenuItem("Add Genre")),
        EDIT_GENRE(new MenuItem("Edit Genre")),
        DELETE_GENRE(new MenuItem("Delete Genre")),
        
        ADD_MUSICIAN(new MenuItem("Add Musician")),
        EDIT_MUSICIAN(new MenuItem("Edit Musician")),
        DELETE_MUSICIAN(new MenuItem("Delete Musician")),
        
        ADD_MUSICIAN_ALBUM(new MenuItem("Add Musician To Album")),
        EDIT_MUSICIAN_ALBUM(new MenuItem("Edit Musician In Album")),
        DELETE_MUSICIAN_ALBUM(new MenuItem("Delete Musician From Album")),
        
        ADD_MUSICIAN_GROUP(new MenuItem("Add Musician To Group")),
        EDIT_MUSICIAN_GROUP(new MenuItem("Edit Musician In Group")),
        DELETE_MUSICIAN_GROUP(new MenuItem("Delete Musician From Group")),
        
        ADD_MUSICIAN_SONG(new MenuItem("Add Musician To Song")),
        EDIT_MUSICIAN_SONG(new MenuItem("Edit Musician In Song")),
        DELETE_MUSICIAN_SONG(new MenuItem("Delete Musician From Song")),
        
        ADD_ARTIST_REFERENCE(new MenuItem("Add Artist Reference")),
        EDIT_ARTIST_REFERENCE(new MenuItem("Edit Artist Reference")),
        DELETE_ARTIST_REFERENCE(new MenuItem("Delete Artist Reference")),
        
        ADD_INSTRUMENT(new MenuItem("Add Instrument")),
        EDIT_INSTRUMENT(new MenuItem("Edit Instrument")),
        DELETE_INSTRUMENT(new MenuItem("Delete Instrument")),
        
        SEPARATOR(new SeparatorMenuItem());
        */
        private final MenuItem item;
        
        private ContextMenuItemType(MenuItem item) {
            this.item = item;
        }      
        public MenuItem get() {
            return item;
        }
}
