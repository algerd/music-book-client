
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.ArtistGenre;
import ru.javafx.musicbook.client.entity.Genre;

public interface ArtistGenreRepository extends CrudRepository<ArtistGenre> {
    
    long countArtistsByGenre(Resource<Genre> genreResource) throws URISyntaxException;
    
    Resource<ArtistGenre> findByArtistAndGenre(Resource<Artist> artist, Resource<Genre> genre) throws URISyntaxException;
    
}
