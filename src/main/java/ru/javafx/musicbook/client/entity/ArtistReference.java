
package ru.javafx.musicbook.client.entity;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@RelPath("artist_references")
public class ArtistReference implements Entity {
    
    private String artist;
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty reference = new SimpleStringProperty("");
    
    public ArtistReference() {}
    
    @Override
    public ArtistReference clone() {
        ArtistReference artistReference = new ArtistReference();
        artistReference.setName(this.getName());
        artistReference.setReference(this.getReference());
        artistReference.setArtist(artist);
        return artistReference;
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
        final ArtistReference other = (ArtistReference) obj;
        if (!Objects.equals(getName(), other.getName())) {
            return false;
        }
        return true;
    }
    
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
    
    public String getReference() {
        return reference.get();
    }
    public void setReference(String value) {
        reference.set(value);
    }
    public StringProperty referenceProperty() {
        return reference;
    }

    @Override
    public String toString() {
        return "ArtistReference{" + "artist=" + artist + ", name=" + name + ", reference=" + reference + '}';
    }  

}
