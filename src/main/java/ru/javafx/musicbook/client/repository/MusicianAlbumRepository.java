
package ru.javafx.musicbook.client.repository;


import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianAlbum;

public interface MusicianAlbumRepository extends CrudRepository<MusicianAlbum> {
    
    Long countByMusicianAndAlbum(Resource<Musician> musician, Resource<Album> album) throws URISyntaxException;
        
    Resource<MusicianAlbum> findByMusicianAndAlbum(Resource<Musician> musician, Resource<Album> album) throws URISyntaxException;
      
}
