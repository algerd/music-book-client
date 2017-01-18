
package ru.javafx.musicbook.client.repository.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.repository.AlbumRepository;

@Repository
public class AlbumRepositoryImpl extends CrudRepositoryImpl<Album> implements AlbumRepository {
    
    public AlbumRepositoryImpl() {
        parameterizedTypeReference = new ParameterizedTypeReference<Resource<Album>>() {};
    }
    
    @Override
    public PagedResources<Resource<Album>> searchByNameAndRatingAndYear(Map<String, Object> parameters) throws URISyntaxException {            
        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "by_name_and_rating_and_year")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.PagedResourcesType<Resource<Album>>() {});       
    }
    
    @Override
    public PagedResources<Resource<Album>> searchByNameAndRatingAndYearAndGenre(Map<String, Object> parameters) throws URISyntaxException {                                    
        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "by_name_and_rating_and_year_and_genre")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.PagedResourcesType<Resource<Album>>() {});              
    } 
       
    @Override
    public void saveGenreInAlbum(Resource<Album> resource, int idGenre) throws URISyntaxException {              
        save(resource.getId().getHref() + "/genres/" + idGenre);
    }   

    @Override
    public void deleteGenreFromAlbum(Resource<Album> resource, int idGenre) throws URISyntaxException {
        delete(resource.getId().getHref() + "/genres/" + idGenre);
    }
    
}
