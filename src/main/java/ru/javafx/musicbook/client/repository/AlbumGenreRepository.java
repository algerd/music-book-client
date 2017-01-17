
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.AlbumGenre;
import ru.javafx.musicbook.client.entity.Genre;

public interface AlbumGenreRepository extends CrudRepository<AlbumGenre> {
    
    Resource<AlbumGenre> findByAlbumAndGenre(Resource<Album> album, Resource<Genre> genre) throws URISyntaxException;
    
}
