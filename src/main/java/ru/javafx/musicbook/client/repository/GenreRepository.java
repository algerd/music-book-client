
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.Song;

public interface GenreRepository extends CrudRepository<Genre> {
    
    Resources<Resource<Genre>> findAllNames() throws URISyntaxException;
    
    Resources<Resource<Genre>> findByArtist(Resource<Artist> resource) throws URISyntaxException;
    
    Resources<Resource<Genre>> findByAlbum(Resource<Album> resource) throws URISyntaxException;
    
    Resources<Resource<Genre>> findBySong(Resource<Song> resource) throws URISyntaxException;
    
    Resources<Resource<Genre>> findByMusician(Resource<Musician> resource) throws URISyntaxException;
}
