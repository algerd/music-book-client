
package ru.javafx.musicbook.client.entity;

import ru.javafx.musicbook.client.datacore.RelPath;
import ru.javafx.musicbook.client.datacore.Entity;
import java.util.Objects;

@RelPath("musician_songs")
public class MusicianSong implements Entity {
    
    private String musician = Musician.DEFAULT_MUSICIAN;
    private String song;
    
    public MusicianSong() {      
    }

    public String getMusician() {
        return musician;
    }

    public void setMusician(String musician) {
        this.musician = musician;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.musician);
        hash = 97 * hash + Objects.hashCode(this.song);
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
        final MusicianSong other = (MusicianSong) obj;
        if (!Objects.equals(this.musician, other.musician)) {
            return false;
        }
        if (!Objects.equals(this.song, other.song)) {
            return false;
        }
        return true;
    }
    
    @Override
    public MusicianSong clone() {
        MusicianSong musicianSong = new MusicianSong();
        musicianSong.setSong(song);
        musicianSong.setMusician(musician);
        return musicianSong;
    }

    @Override
    public String toString() {
        return "MusicianSong{" + "musician=" + musician + ", song=" + song + '}';
    }

}
