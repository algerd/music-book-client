
package ru.javafx.musicbook.client.entity;

import ru.javafx.musicbook.client.datacore.RelPath;
import ru.javafx.musicbook.client.datacore.Entity;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@RelPath("musicians")
public class Musician implements Entity {
    
    // Default: Unknown musician with id = 1
    public static final String DEFAULT_MUSICIAN = "http://localhost:8080/api/musicians/1";
    
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty dateOfBirth = new SimpleStringProperty("");
    private final StringProperty dateOfDeath = new SimpleStringProperty("");
    private final StringProperty country = new SimpleStringProperty("");
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
    private final StringProperty description = new SimpleStringProperty("");   
    
    public Musician() {}
    
    public String getName() {
        return name.get();
    }
    public void setName(String value) {
        name.set(value);
    }
    public StringProperty nameProperty() {
        return name;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth.get();
    }
    public void setDateOfBirth(String value) {
        dateOfBirth.set(value);
    }
    public StringProperty dateOfBirthProperty() {
        return dateOfBirth;
    }
    
    public String getDateOfDeath() {
        return dateOfDeath.get();
    }
    public void setDateOfDeath(String value) {
        dateOfDeath.set(value);
    }
    public StringProperty dateOfDeathProperty() {
        return dateOfDeath;
    }
    
    public String getCountry() {
        return country.get();
    }
    public void setCountry(String value) {
        country.set(value);
    }
    public StringProperty countryProperty() {
        return country;
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
    public Musician clone() {
        Musician musician = new Musician();
        musician.setName(getName());
        musician.setDateOfBirth(getDateOfBirth());
        musician.setDateOfDeath(getDateOfDeath());
        musician.setCountry(getCountry());
        musician.setRating(getRating());
        musician.setDescription(getDescription());
        return musician;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final Musician other = (Musician) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Musician{" + "name=" + name + ", dateOfBirth=" + dateOfBirth + ", dateOfDeath=" + dateOfDeath + ", country=" + country + ", rating=" + rating + ", description=" + description + '}';
    }   
    
}
