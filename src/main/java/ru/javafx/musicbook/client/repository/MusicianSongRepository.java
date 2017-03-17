
package ru.javafx.musicbook.client.repository;

import ru.javafx.musicbook.client.datacore.repository.CrudRepository;
import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianSong;
import ru.javafx.musicbook.client.entity.Song;

public interface MusicianSongRepository extends CrudRepository<MusicianSong> {
    
    Long countByMusicianAndSong(Resource<Musician> musician, Resource<Song> song) throws URISyntaxException;
        
    Resource<MusicianSong> findByMusicianAndSong(Resource<Musician> musician, Resource<Song> song) throws URISyntaxException;
     
}
