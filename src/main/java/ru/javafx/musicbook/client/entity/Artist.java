
package ru.javafx.musicbook.client.entity;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@RelPath("artists")
public class Artist implements Entity {
     
    private final StringProperty name = new SimpleStringProperty("");
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
    private final StringProperty description = new SimpleStringProperty("");
    
    public Artist() {}
   
    public String getName() {
        return name.get();
    }
    public void setName(String value) {
        name.set(value);
    }
    public StringProperty nameProperty() {
        return name;
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
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.name);
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
        final Artist other = (Artist) obj;
        return Objects.equals(this.name, other.name);
    }
    
    @Override
    public Artist clone() {
        Artist artist = new Artist();
        artist.setName(this.getName());
        artist.setRating(this.getRating());
        artist.setDescription(this.getDescription());
        return artist;
    }

    @Override
    public String toString() {
        return "Artist{" + "name=" + name + ", rating=" + rating + ", description=" + description + '}';
    }
       
}
