
package ru.javafx.musicbook.client.repository.impl;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.repository.AlbumRepository;

@Repository
public class AlbumRepositoryImpl extends CrudRepositoryImpl<Album> implements AlbumRepository {
    
    public AlbumRepositoryImpl() {  
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Album>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Album>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Album>>() {};
    }
    
    @Override
    public PagedResources<Resource<Album>> searchByNameAndRatingAndYear(Map<String, Object> parameters) throws URISyntaxException {            
        return getPagedResources(parameters, new String[]{relPath, "search", "by_name_and_rating_and_year"});
    }
    
    @Override
    public PagedResources<Resource<Album>> searchByNameAndRatingAndYearAndGenre(Map<String, Object> parameters) throws URISyntaxException {                                    
        return getPagedResources(parameters, new String[]{relPath, "search", "by_name_and_rating_and_year_and_genre"});        
    } 

}
