
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;


public interface ArtistRepository extends CrudRepository<Artist> {
    
    PagedResources<Resource<Artist>> search(String relPath) throws URISyntaxException;
       
    Resources<Resource<Artist>> findAllNames() throws URISyntaxException;
      
    Resources<Resource<Artist>> findByGenre(Resource<Genre> genreResource) throws URISyntaxException;
      
}
