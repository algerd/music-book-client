
package ru.javafx.musicbook.client.entity;

import ru.javafx.musicbook.client.datacore.RelPath;
import ru.javafx.musicbook.client.datacore.Entity;
import java.util.Objects;

@RelPath("artist_genres")
public class ArtistGenre implements Entity {
    
    private String artist = Artist.DEFAULT_ARTIST;
    private String genre = Genre.DEFAULT_GENRE;
    
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
    public ArtistGenre clone() {
        ArtistGenre artistGenre = new ArtistGenre();
        artistGenre.setArtist(artist);
        artistGenre.setGenre(genre);
        return artistGenre;
    }

    @Override
    public String toString() {
        return "ArtistGenre{" + "artist=" + artist + ", genre=" + genre + '}';
    }

}
