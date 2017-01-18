
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;


public interface ArtistRepository extends CrudRepository<Artist> {
       
    Resources<Resource<Artist>> findAllNames() throws URISyntaxException;
      
    Resources<Resource<Artist>> findByGenre(Resource<Genre> genreResource) throws URISyntaxException;
       
    PagedResources<Resource<Artist>> searchByNameAndRating(Map<String, Object> parameters) throws URISyntaxException;

    PagedResources<Resource<Artist>> searchByGenreAndRatingAndName(Map<String, Object> parameters) throws URISyntaxException;
    
    Resource<Artist> saveAndGetResource(Artist entity) throws URISyntaxException;
}
