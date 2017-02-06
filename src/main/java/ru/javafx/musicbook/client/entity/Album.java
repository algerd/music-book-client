
package ru.javafx.musicbook.client.entity;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@RelPath("albums")
public class Album implements Entity {

    // Default: Unknown album with id = 1
    public static final String DEFAULT_ALBUM = "http://localhost:8080/api/albums/1";
    
    private String artist = Artist.DEFAULT_ARTIST;
    private final StringProperty name = new SimpleStringProperty("");
    private final IntegerProperty year = new SimpleIntegerProperty(0);
    private final StringProperty time = new SimpleStringProperty("");
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
    private final StringProperty description = new SimpleStringProperty("");     

    public Album() {}
    
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }  
      
    public String getName() {
        return name.get();
    }
    public void setName(String value) {
        name.set(value);
    }
    public StringProperty nameProperty() {
        return name;
    }
    
    public int getYear() {
        return year.get();
    }
    public void setYear(int value) {
        year.set(value);
    }
    public IntegerProperty yearProperty() {
        return year;
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
    public Album clone() {
        Album album = new Album();
        album.setName(getName());
        album.setYear(getYear());
        album.setTime(getTime());
        album.setRating(getRating());
        album.setDescription(getDescription());
        return album;
    }
 
    @Override
    public int hashCode() {
        int hash = 5;
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
        final Album other = (Album) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "Album{" + "name=" + name + ", year=" + year + ", time=" + time + ", rating=" + rating + ", description=" + description + '}';
    }
    
}
