
package ru.javafx.musicbook.client.entity;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@RelPath("songs")
public class Song implements Entity {

    private final StringProperty name = new SimpleStringProperty("");
    private final IntegerProperty track = new SimpleIntegerProperty(0);
    private final StringProperty lyric = new SimpleStringProperty("");
    private final StringProperty time = new SimpleStringProperty("");
    private final IntegerProperty rating = new SimpleIntegerProperty(0);   
    private final StringProperty description = new SimpleStringProperty("");
    
    public Song() {}
    
    public String getName() {
        return name.get();
    }
    public void setName(String value) {
        name.set(value);
    }
    public StringProperty nameProperty() {
        return name;
    }

    public int getTrack() {
        return track.get();
    }
    public void setTrack(int value) {
        track.set(value);
    }
    public IntegerProperty trackProperty() {
        return track;
    }

    public String getLyric() {
        return lyric.get();
    }
    public void setLyric(String value) {
        lyric.set(value);
    }
    public StringProperty lyricProperty() {
        return lyric;
    }

    public String getTime() {
        return time.get();
    }
    public void setTime(String value) {
        time.set(value);
    }
    public StringProperty timeProperty() {
        return time;
    }

    public int getRating() {
        return rating.get();
    }
    public void setRating(int value) {
        rating.set(value);
    }
    public IntegerProperty ratingProperty() {
        return rating;
    }

    public String getDescription() {
        return description.get();
    }
    public void setDescription(String value) {
        description.set(value);
    }
    public StringProperty descriptionProperty() {
        return description;
    }
    
    @Override
    public Song clone() {
        Song song = new Song();
        song.setName(getName());
        song.setTrack(getTrack());
        song.setLyric(getLyric());
        song.setTime(getTime());
        song.setRating(getRating());
        song.setDescription(getDescription());
        return song;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
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
        final Song other = (Song) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "Song{" + "name=" + name + ", track=" + track + ", lyric=" + lyric + ", time=" + time + ", rating=" + rating + ", description=" + description + '}';
    }   
      
}
