
package ru.javafx.musicbook.client.repository;


import ru.javafx.musicbook.client.datacore.CrudRepository;
import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGroup;

public interface MusicianGroupRepository extends CrudRepository<MusicianGroup> {
    
    Long countByMusicianAndArtist(Resource<Musician> musician, Resource<Artist> artist) throws URISyntaxException;
        
    Resource<MusicianGroup> findByMusicianAndArtist(Resource<Musician> musician, Resource<Artist> artist) throws URISyntaxException;
      
}
