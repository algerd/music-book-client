
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.AlbumGenre;
import ru.javafx.musicbook.client.entity.Genre;

public interface AlbumGenreRepository extends CrudRepository<AlbumGenre> {
    
    long countAlbumsByGenre(Resource<Genre> genreResource) throws URISyntaxException;
    
    Resource<AlbumGenre> findByAlbumAndGenre(Resource<Album> album, Resource<Genre> genre) throws URISyntaxException;
    
    //Resources<Resource<Album>> findAlbumsByGenre(Resource<Genre> genreResource) throws URISyntaxException;
}
