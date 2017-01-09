
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.controller.paginator.Paginator;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.Genre;

public interface GenreRepository extends CrudRepository<Genre> {
    
    long countArtistsByGenre(Resource<Genre> genreResource) throws URISyntaxException;
    
    PagedResources<Resource<Genre>> searchByName(Paginator paginator, String search) throws URISyntaxException;
    
    Resources<Resource<Genre>> findAll() throws URISyntaxException;
    
    Resources<Resource<Genre>> findByArtist(Resource<? extends Entity> artistResource) throws URISyntaxException;
    
}
