
package ru.javafx.musicbook.client.entity;

import java.util.Objects;

@RelPath("musician_albums")
public class MusicianAlbum  implements Entity {

    private String musician = Musician.DEFAULT_MUSICIAN;
    private String album = Album.DEFAULT_ALBUM;
    
    public MusicianAlbum() {        
    }

    public String getMusician() {
        return musician;
    }
    public void setMusician(String musician) {
        this.musician = musician;
    }

    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.musician);
        hash = 71 * hash + Objects.hashCode(this.album);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MusicianAlbum other = (MusicianAlbum) obj;
        if (!Objects.equals(this.musician, other.musician)) {
            return false;
        }
        if (!Objects.equals(this.album, other.album)) {
            return false;
        }
        return true;
    }
    
    @Override
    public MusicianAlbum clone() {
        MusicianAlbum musicianAlbum = new MusicianAlbum();
        musicianAlbum.setAlbum(album);
        musicianAlbum.setMusician(musician);
        return musicianAlbum;
    }

    
}
