
package ru.javafx.musicbook.client.entity;

import ru.javafx.musicbook.client.datacore.RelPath;
import ru.javafx.musicbook.client.datacore.Entity;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@RelPath("musician_groups")
public class MusicianGroup  implements Entity {
    
    private final StringProperty startDate = new SimpleStringProperty("");
    private final StringProperty endDate = new SimpleStringProperty("");
    private String musician = Musician.DEFAULT_MUSICIAN;
    private String artist = Artist.DEFAULT_ARTIST;
    
    public MusicianGroup() {        
    }
    
    public String getStartDate() {
        return startDate.get();
    }
    public void setStartDate(String value) {
        startDate.set(value);
    }
    public StringProperty startDateProperty() {
        return startDate;
    }
    
    public String getEndDate() {
        return endDate.get();
    }
    public void setEndDate(String value) {
        endDate.set(value);
    }
    public StringProperty endDateProperty() {
        return endDate;
    }

    public String getMusician() {
        return musician;
    }
    public void setMusician(String musician) {
        this.musician = musician;
    }

    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.musician);
        hash = 71 * hash + Objects.hashCode(this.artist);
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
        final MusicianGroup other = (MusicianGroup) obj;
        if (!Objects.equals(this.musician, other.musician)) {
            return false;
        }
        if (!Objects.equals(this.artist, other.artist)) {
            return false;
        }
        return true;
    }
    
    @Override
    public MusicianGroup clone() {
        MusicianGroup musicianGroup = new MusicianGroup();
        musicianGroup.setStartDate(getStartDate());
        musicianGroup.setEndDate(getEndDate());
        musicianGroup.setArtist(artist);
        musicianGroup.setMusician(musician);
        return musicianGroup;
    }

    @Override
    public String toString() {
        return "MusicianGroup{" + "startDate=" + startDate + ", endDate=" + endDate + ", musician=" + musician + ", artist=" + artist + '}';
    }
    
}
