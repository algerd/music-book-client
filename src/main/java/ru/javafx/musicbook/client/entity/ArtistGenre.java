
package ru.javafx.musicbook.client.entity;

import java.util.Objects;

@RelPath("artist_genres")
public class ArtistGenre implements Entity {
    
    // Default: Unknown artist with id = 1
    private String artist = "http://localhost:8080/api/artists/1";
   
    // Default: Unknown genre with id = 1
    private String genre = "http://localhost:8080/api/genres/1";
    
    public ArtistGenre() {}

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.artist);
        hash = 37 * hash + Objects.hashCode(this.genre);
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
        final ArtistGenre other = (ArtistGenre) obj;
        if (!Objects.equals(this.artist, other.artist)) {
            return false;
        }
        if (!Objects.equals(this.genre, other.genre)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ArtistGenre{" + "artist=" + artist + ", genre=" + genre + '}';
    }

}
