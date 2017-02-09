
package ru.javafx.musicbook.client.entity;

import java.util.Objects;

@RelPath("musician_instruments")
public class MusicianInstrument implements Entity {
    
    private String musician = Musician.DEFAULT_MUSICIAN;
    private String instrument = Instrument.DEFAULT_INSTRUMENT;
    
    public MusicianInstrument() {}

    public String getMusician() {
        return musician;
    }

    public void setMusician(String musician) {
        this.musician = musician;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.musician);
        hash = 97 * hash + Objects.hashCode(this.instrument);
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
        final MusicianInstrument other = (MusicianInstrument) obj;
        if (!Objects.equals(this.musician, other.musician)) {
            return false;
        }
        if (!Objects.equals(this.instrument, other.instrument)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MusicianGenre{" + "musician=" + musician + ", instrument=" + instrument + '}';
    }
        
}
