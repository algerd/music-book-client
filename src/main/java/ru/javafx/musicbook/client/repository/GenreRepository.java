
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;

public interface GenreRepository extends CrudRepository<Genre> {
    
    Resources<Resource<Genre>> findAllNames() throws URISyntaxException;
    
    Resources<Resource<Genre>> findByArtist(Resource<Artist> resource) throws URISyntaxException;
    
    Resources<Resource<Genre>> findByAlbum(Resource<Album> resource) throws URISyntaxException;
    
}
