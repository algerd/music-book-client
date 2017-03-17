
package ru.javafx.musicbook.client.repository.impl;

import ru.javafx.musicbook.client.datacore.repository.impl.CrudRepositoryImpl;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGroup;
import ru.javafx.musicbook.client.repository.MusicianGroupRepository;

@Repository
public class MusicianGroupRepositoryImpl extends CrudRepositoryImpl<MusicianGroup> implements MusicianGroupRepository {

    public MusicianGroupRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<MusicianGroup>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<MusicianGroup>>>() {};   
    }
    
    @Override
    public Resource<MusicianGroup> findByMusicianAndArtist(Resource<Musician> musician, Resource<Artist> artist) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        parameters.put("artist", artist.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByMusicianAndArtist"});
    }
    
    @Override
    public Long countByMusicianAndArtist(Resource<Musician> musician, Resource<Artist> artist) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        parameters.put("artist", artist.getId().getHref());
        return countParameterizedQuery(parameters, new String[]{relPath, "search", "countByMusicianAndArtist"});
    }

}
