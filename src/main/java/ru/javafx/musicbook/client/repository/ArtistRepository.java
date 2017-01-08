
package ru.javafx.musicbook.client.repository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.controller.paginator.Paginator;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.impl.CrudRepositoryImpl;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@Repository
public class ArtistRepository extends CrudRepositoryImpl<Artist> {

    // перенести в artistGenreRepository и повесить апдейт-ченджера(раскомментировать) 
    public void saveGenreInArtist(Resource<? extends Entity> resource, int idGenre) {              
        try {
            Resource<Artist> oldResource = new Traverson(new URI(resource.getId().getHref()), MediaTypes.HAL_JSON)//
                        .follow("self")
                        .withHeaders(sessionManager.createSessionHeaders())
                        .toObject(new ParameterizedTypeReference<Resource<Artist>>() {});
            postAbs(resource.getId().getHref() + "/genres/" + idGenre);    
            //super.setUpdated(new WrapChangedEntity<>(oldResource, (Resource<Artist>)resource));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }   
 
    // перенести в artistGenreRepository и повесить делит-ченджера(раскомментировать)
    public void deleteGenreFromArtist(Resource<? extends Entity> resource, int idGenre) {
        try {
            Resource<Artist> oldResource = new Traverson(new URI(resource.getId().getHref()), MediaTypes.HAL_JSON)//
                        .follow("self")
                        .withHeaders(sessionManager.createSessionHeaders())
                        .toObject(new ParameterizedTypeReference<Resource<Artist>>() {});
            deleteAbs(resource.getId().getHref() + "/genres/" + idGenre);    
            //super.setUpdated(new WrapChangedEntity<>(oldResource, (Resource<Artist>)resource));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }
        
    public PagedResources<Resource<Artist>> searchByNameAndRating(Paginator paginator, int minRating, int maxRating, String search) throws URISyntaxException {            
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("minrating", minRating);
        parameters.put("maxrating", maxRating);
        parameters.put("search", search);
        parameters.putAll(paginator.getParameters());
        PagedResources<Resource<Artist>> resource = new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "by_name_and_rating")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.PagedResourcesType<Resource<Artist>>() {});        
        paginator.setTotalElements((int) resource.getMetadata().getTotalElements());
        return resource;       
    }
    
    public PagedResources<Resource<Artist>> searchByGenreAndRatingAndName(Paginator paginator, int minRating, int maxRating, String search, Resource<Genre> resourceGenre) throws URISyntaxException {                   
        if (resourceGenre == null || resourceGenre.getContent().getName().equals("All genres")) {
            return searchByNameAndRating(paginator, minRating, maxRating, search);
        }       
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("minrating", minRating);
        parameters.put("maxrating", maxRating);
        parameters.put("search", search);
        parameters.put("id_genre", Helper.getId(resourceGenre));        
        parameters.putAll(paginator.getParameters());                  
        PagedResources<Resource<Artist>> resource = new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "by_genre_and_rating_and_name")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.PagedResourcesType<Resource<Artist>>() {});        
        paginator.setTotalElements((int) resource.getMetadata().getTotalElements());
        return resource;       
    } 
    
    public Resources<Resource<Artist>> findByGenre(Resource<Genre> genreResource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id_genre", Helper.getId(genreResource));
        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "by_genre")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resources<Resource<Artist>>>() {});      
    } 
}
