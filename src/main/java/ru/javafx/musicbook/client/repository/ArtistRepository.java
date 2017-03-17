
package ru.javafx.musicbook.client.repository;

import ru.javafx.musicbook.client.datacore.repository.CrudRepository;
import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;


public interface ArtistRepository extends CrudRepository<Artist> {
           
    Resources<Resource<Artist>> findAllNames() throws URISyntaxException;
      
    Resources<Resource<Artist>> findByGenre(Resource<Genre> genreResource) throws URISyntaxException;
      
}
