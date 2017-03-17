
package ru.javafx.musicbook.client.repository.impl;

import ru.javafx.musicbook.client.datacore.repository.impl.CrudRepositoryImpl;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.repository.SongRepository;

@Repository
public class SongRepositoryImpl extends CrudRepositoryImpl<Song> implements SongRepository {
    
    public SongRepositoryImpl() {  
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Song>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Song>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Song>>() {};
    }
    
    @Override
    public Resources<Resource<Song>> findByGenre(Resource<Genre> resource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("genre", resource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_genre"});       
    }
    
    @Override
    public Resources<Resource<Song>> findByAlbum(Resource<Album> resource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("album", resource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_album"});
    }

}
