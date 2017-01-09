
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.controller.paginator.Paginator;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.Genre;


public interface ArtistRepository extends CrudRepository<Artist> {
    
    void saveGenreInArtist(Resource<? extends Entity> resource, int idGenre) throws URISyntaxException;
    
    void deleteGenreFromArtist(Resource<? extends Entity> resource, int idGenre) throws URISyntaxException;
    
    PagedResources<Resource<Artist>> searchByNameAndRating(Paginator paginator, int minRating, int maxRating, String search) throws URISyntaxException;
    
    PagedResources<Resource<Artist>> searchByGenreAndRatingAndName(Paginator paginator, int minRating, int maxRating, String search, Resource<Genre> resourceGenre) throws URISyntaxException;
    
    Resources<Resource<Artist>> findByGenre(Resource<Genre> genreResource) throws URISyntaxException;
    
}
