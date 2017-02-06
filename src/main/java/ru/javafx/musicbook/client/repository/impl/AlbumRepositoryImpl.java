
package ru.javafx.musicbook.client.repository.impl;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.AlbumRepository;

@Repository
public class AlbumRepositoryImpl extends CrudRepositoryImpl<Album> implements AlbumRepository {
    
    public AlbumRepositoryImpl() {  
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Album>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Album>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Album>>() {};
    }
   
    @Override
    public Resources<Resource<Album>> findByGenre(Resource<Genre> resource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("genre", resource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_genre"});       
    }
    
    @Override
    public Resources<Resource<Album>> findByArtist(Resource<Artist> resource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("artist", resource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_artist"});
    }
    
}
