
package ru.javafx.musicbook.client.repository;

import ru.javafx.musicbook.client.datacore.repository.CrudRepository;
import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.AlbumGenre;
import ru.javafx.musicbook.client.entity.Genre;

public interface AlbumGenreRepository extends CrudRepository<AlbumGenre> {
    
    long countAlbumsByGenre(Resource<Genre> genreResource) throws URISyntaxException;
    
    Resource<AlbumGenre> findByAlbumAndGenre(Resource<Album> album, Resource<Genre> genre) throws URISyntaxException;
    
    long countByAlbumAndGenre(Resource<Album> album, Resource<Genre> genre) throws URISyntaxException;
}
