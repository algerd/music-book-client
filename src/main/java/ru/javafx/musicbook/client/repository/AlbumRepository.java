
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Genre;

public interface AlbumRepository extends CrudRepository<Album> {
        
    Resources<Resource<Album>> findByGenre(Resource<Genre> genreResource) throws URISyntaxException;
}
