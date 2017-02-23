
package ru.javafx.musicbook.client.entity;

import java.util.Objects;

@RelPath("song_genres")
public class SongGenre implements Entity {
    
    private String song;
    private String genre = Genre.DEFAULT_GENRE;
    
    public SongGenre() {}

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
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
        hash = 97 * hash + Objects.hashCode(this.song);
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
        final SongGenre other = (SongGenre) obj;
        if (!Objects.equals(this.song, other.song)) {
            return false;
        }
        if (!Objects.equals(this.genre, other.genre)) {
            return false;
        }
        return true;
    }
    
    @Override
    public SongGenre clone() {
        SongGenre songGenre = new SongGenre();
        songGenre.setSong(song);
        songGenre.setGenre(genre);
        return songGenre;
    }

    @Override
    public String toString() {
        return "AlbumGenre{" + "song=" + song + ", genre=" + genre + '}';
    }
        
}
