
package ru.javafx.musicbook.client.repository;


import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGroup;

public interface MusicianGroupRepository extends CrudRepository<MusicianGroup> {
        
    Resource<MusicianGroup> findByMusicianAndArtist(Resource<Musician> musician, Resource<Artist> artist) throws URISyntaxException;
    
    Resource<MusicianGroup> findByMusician(Resource<Musician> musician) throws URISyntaxException;
    
    Resource<MusicianGroup> findByArtist(Resource<Artist> artist) throws URISyntaxException;
        
}
