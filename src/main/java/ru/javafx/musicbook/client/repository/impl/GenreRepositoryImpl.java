
package ru.javafx.musicbook.client.repository.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.controller.paginator.Paginator;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.GenreRepository;
import org.springframework.core.ParameterizedTypeReference;

@Repository
public class GenreRepositoryImpl extends CrudRepositoryImpl<Genre> implements GenreRepository {

    public GenreRepositoryImpl() {    
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Genre>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Genre>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Genre>>() {};
    }
    
    @Override
    public PagedResources<Resource<Genre>> searchByName(Paginator paginator, String search) throws URISyntaxException {    
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("search", search);
        parameters.putAll(paginator.getParameters()); 
        PagedResources<Resource<Genre>> resources = getPagedResources(parameters, relPath, "search", "by_name");       
        paginator.setTotalElements((int) resources.getMetadata().getTotalElements());
        return resources;       
    }
   
    @Override
    public Resources<Resource<Genre>> findAllNames() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("projection", "get_name"); 
        parameters.put("sort", "name,asc"); 
        return getParameterizedResources(parameters);     
    }
    
    @Override
    public Resources<Resource<Genre>> findByArtist(Resource<Artist> resource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("artist", resource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_artist"});       
    } 
    
    @Override
    public Resources<Resource<Genre>> findByAlbum(Resource<Album> resource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("album", resource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_album"});
    }

}
