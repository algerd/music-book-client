
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
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.utils.Helper;

@Repository
public class ArtistRepositoryImpl extends CrudRepositoryImpl<Artist> implements ArtistRepository {
    
    public ArtistRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Artist>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Artist>>>() {};
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Artist>>() {};
    }
    
    @Override
    public PagedResources<Resource<Artist>> searchByNameAndRating(Map<String, Object> parameters) throws URISyntaxException {            
        return getPagedResources(parameters, new String[] {relPath, "search", "by_name_and_rating"});
    }
    
    @Override
    public PagedResources<Resource<Artist>> searchByGenreAndRatingAndName(Map<String, Object> parameters) throws URISyntaxException {                                    
        return getPagedResources(parameters, new String[] {relPath, "search", "by_genre_and_rating_and_name"});
    } 
       
    @Override
    public Resources<Resource<Artist>> findByGenre(Resource<Genre> genreResource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id_genre", Helper.getId(genreResource));
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_genre"});       
    } 
  
    @Override
    public Resources<Resource<Artist>> findAllNames() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("projection", "get_name"); 
        parameters.put("sort", "name,asc"); 
        return getParameterizedResources(parameters);
    }

}
