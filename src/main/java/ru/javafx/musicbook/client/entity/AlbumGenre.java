
package ru.javafx.musicbook.client.entity;

import ru.javafx.musicbook.client.datacore.entity.RelPath;
import ru.javafx.musicbook.client.datacore.entity.Entity;
import java.util.Objects;

@RelPath("album_genres")
public class AlbumGenre implements Entity {
    
    private String album = Album.DEFAULT_ALBUM;
    private String genre = Genre.DEFAULT_GENRE;
    
    public AlbumGenre() {}

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.album);
        hash = 97 * hash + Objects.hashCode(this.genre);
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
        final AlbumGenre other = (AlbumGenre) obj;
        if (!Objects.equals(this.album, other.album)) {
            return false;
        }
        if (!Objects.equals(this.genre, other.genre)) {
            return false;
        }
        return true;
    }
    
    @Override
    public AlbumGenre clone() {
        AlbumGenre albumGenre = new AlbumGenre();
        albumGenre.setAlbum(album);
        albumGenre.setGenre(genre);
        return albumGenre;
    }

    @Override
    public String toString() {
        return "AlbumGenre{" + "album=" + album + ", genre=" + genre + '}';
    }
        
}
