
package ru.javafx.musicbook.client.entity;

import ru.javafx.musicbook.client.datacore.RelPath;
import ru.javafx.musicbook.client.datacore.Entity;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@RelPath("instruments")
public class Instrument implements Entity {
    
    // Default: Unknown instrument with id = 1
    public static final String DEFAULT_INSTRUMENT = "http://localhost:8080/api/instruments/1";

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    
    public Instrument() {}
   
    public String getName() {
        return name.get();
    }
    public void setName(String value) {
        name.set(value);
    }
    public StringProperty nameProperty() {
        return name;
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
    public Instrument clone() {
        Instrument instrument = new Instrument();
        instrument.setName(this.getName());
        instrument.setDescription(this.getDescription());
        return instrument;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
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
        final Instrument other = (Instrument) obj;
        if (!Objects.equals(getName(), other.getName())) {
            return false;
        }
        return true;
    }  

    @Override
    public String toString() {
        return getName();
    }

}
