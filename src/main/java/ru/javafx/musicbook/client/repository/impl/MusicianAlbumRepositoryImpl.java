
package ru.javafx.musicbook.client.repository.impl;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianAlbum;
import ru.javafx.musicbook.client.repository.MusicianAlbumRepository;

@Repository
public class MusicianAlbumRepositoryImpl extends CrudRepositoryImpl<MusicianAlbum> implements MusicianAlbumRepository {

    public MusicianAlbumRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<MusicianAlbum>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<MusicianAlbum>>>() {};   
    }
    
    @Override
    public Resource<MusicianAlbum> findByMusicianAndAlbum(Resource<Musician> musician, Resource<Album> album) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        parameters.put("album", album.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByMusicianAndAlbum"});
    }
    
    @Override
    public Long countByMusicianAndAlbum(Resource<Musician> musician, Resource<Album> album) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        parameters.put("album", album.getId().getHref());
        return countParameterizedQuery(parameters, new String[]{relPath, "search", "countByMusicianAndAlbum"});
    }
    
    @Override
    public Resource<MusicianAlbum> findByMusician(Resource<Musician> musician) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByMusician"});
    }
    
    @Override
    public Resource<MusicianAlbum> findByAlbum(Resource<Album> album) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("album", album.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByAlbum"});
    }
  
}
