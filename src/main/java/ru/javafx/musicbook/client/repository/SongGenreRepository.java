
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.entity.SongGenre;

public interface SongGenreRepository extends CrudRepository<SongGenre> {
    
    long countSongsByGenre(Resource<Genre> genreResource) throws URISyntaxException;
    
    Resource<SongGenre> findBySongAndGenre(Resource<Song> song, Resource<Genre> genre) throws URISyntaxException;

}
