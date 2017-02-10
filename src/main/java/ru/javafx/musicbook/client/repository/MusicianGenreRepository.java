
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGenre;

public interface MusicianGenreRepository extends CrudRepository<MusicianGenre> {
    
    Resource<MusicianGenre> findByMusicianAndGenre(Resource<Musician> artist, Resource<Genre> genre) throws URISyntaxException;
    
    long countMusiciansByGenre(Resource<Genre> genreResource) throws URISyntaxException;
    
}
