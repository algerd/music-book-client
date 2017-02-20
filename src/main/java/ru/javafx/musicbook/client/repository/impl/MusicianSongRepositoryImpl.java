
package ru.javafx.musicbook.client.repository.impl;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianSong;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.repository.MusicianSongRepository;

@Repository
public class MusicianSongRepositoryImpl extends CrudRepositoryImpl<MusicianSong> implements MusicianSongRepository {

    public MusicianSongRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<MusicianSong>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<MusicianSong>>>() {};   
    }
    
    @Override
    public Resource<MusicianSong> findByMusicianAndSong(Resource<Musician> musician, Resource<Song> song) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        parameters.put("song", song.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByMusicianAndSong"});
    }
    
    @Override
    public Long countByMusicianAndSong(Resource<Musician> musician, Resource<Song> song) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        parameters.put("song", song.getId().getHref());
        return countParameterizedQuery(parameters, new String[]{relPath, "search", "countByMusicianAndSong"});
    }
    
    @Override
    public Resource<MusicianSong> findByMusician(Resource<Musician> musician) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByMusician"});
    }
    
    @Override
    public Resource<MusicianSong> findBySong(Resource<Song> song) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("song", song.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findBySong"});
    }
  
}
