
package ru.javafx.musicbook.client.repository;

import ru.javafx.musicbook.client.datacore.CrudRepository;
import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;

public interface AlbumRepository extends CrudRepository<Album> {
    
    Resources<Resource<Album>> findByArtist(Resource<Artist> resource) throws URISyntaxException;
           
    Resources<Resource<Album>> findByGenre(Resource<Genre> resource) throws URISyntaxException;
    
    Resources<Resource<Album>> findAllNames() throws URISyntaxException;
}
