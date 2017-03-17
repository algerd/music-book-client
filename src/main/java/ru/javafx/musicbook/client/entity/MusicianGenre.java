
package ru.javafx.musicbook.client.entity;

import ru.javafx.musicbook.client.datacore.entity.RelPath;
import ru.javafx.musicbook.client.datacore.entity.Entity;
import java.util.Objects;

@RelPath("musician_genres")
public class MusicianGenre implements Entity {
    
    private String musician = Musician.DEFAULT_MUSICIAN;
    private String genre = Genre.DEFAULT_GENRE;
    
    public MusicianGenre() {}

    public String getMusician() {
        return musician;
    }

    public void setMusician(String musician) {
        this.musician = musician;
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
        hash = 97 * hash + Objects.hashCode(this.musician);
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
        final MusicianGenre other = (MusicianGenre) obj;
        if (!Objects.equals(this.musician, other.musician)) {
            return false;
        }
        if (!Objects.equals(this.genre, other.genre)) {
            return false;
        }
        return true;
    }
    
    @Override
    public MusicianGenre clone() {
        MusicianGenre musicianGenre = new MusicianGenre();
        musicianGenre.setMusician(musician);
        musicianGenre.setGenre(genre);
        return musicianGenre;
    }

    @Override
    public String toString() {
        return "MusicianGenre{" + "musician=" + musician + ", genre=" + genre + '}';
    }
        
}
