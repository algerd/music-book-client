
package ru.javafx.musicbook.client.entity;

public class ArtistGenre implements Entity {
    
    private int idArtist;
    private int idGenre;

    public ArtistGenre() {
    }
    
    public int getIdArtist() {
        return idArtist;
    }

    public void setIdArtist(int idArtist) {
        this.idArtist = idArtist;
    }

    public int getIdGenre() {
        return idGenre;
    }

    public void setIdGenre(int idGenre) {
        this.idGenre = idGenre;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.idArtist;
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
        final ArtistGenre other = (ArtistGenre) obj;
        if (this.idArtist != other.idArtist) {
            return false;
        }
        if (this.idGenre != other.idGenre) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ArtistGenre{" + "idArtist=" + idArtist + ", idGenre=" + idGenre + '}';
    }

}
