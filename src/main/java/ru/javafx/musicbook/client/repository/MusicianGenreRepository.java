
package ru.javafx.musicbook.client.repository;

import ru.javafx.musicbook.client.datacore.CrudRepository;
import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGenre;

public interface MusicianGenreRepository extends CrudRepository<MusicianGenre> {
    
    Resource<MusicianGenre> findByMusicianAndGenre(Resource<Musician> musician, Resource<Genre> genre) throws URISyntaxException;
    
    long countByMusicianAndGenre(Resource<Musician> musician, Resource<Genre> genre) throws URISyntaxException;
    
    long countMusiciansByGenre(Resource<Genre> genreResource) throws URISyntaxException;
    
}
