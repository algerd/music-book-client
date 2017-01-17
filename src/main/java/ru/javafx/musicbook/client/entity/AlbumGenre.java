
package ru.javafx.musicbook.client.entity;

@RelPath("album_genres")
public class AlbumGenre implements Entity {
    
    // Default: Unknown album with id = 1
    private String album = "http://localhost:8080/api/artists/1";
   
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
        
}
