
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Song;

public interface SongRepository extends CrudRepository<Song> {
    
    Resources<Resource<Song>> findByAlbum(Resource<Album> resource) throws URISyntaxException;
    
    Resources<Resource<Song>> findByGenre(Resource<Genre> resource) throws URISyntaxException;

}
