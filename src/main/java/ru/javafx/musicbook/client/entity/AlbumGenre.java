
package ru.javafx.musicbook.client.entity;

import java.util.Objects;

@RelPath("album_genres")
public class AlbumGenre implements Entity {
    
    // Default: Unknown album with id = 1
    private String album = "http://localhost:8080/api/albums/1";
   
    // Default: Unknown genre with id = 1
    private String genre = "http://localhost:8080/api/genres/1";
    
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
    public String toString() {
        return "AlbumGenre{" + "album=" + album + ", genre=" + genre + '}';
    }
        
}
