
package ru.javafx.musicbook.client.repository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.controller.paginator.Paginator;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.impl.CrudRepositoryImpl;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@Repository
public class ArtistRepository extends CrudRepositoryImpl<Artist> {
    
    private static final String REL_PATH = "artists";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /*   
    public void save(Artist artist) {
        requestService.post(REL_PATH, artist);
    }
    */
    public Resource<Artist> saveAndGetResource(Artist artist) {
        Resource<Artist> resource = new Traverson(requestService.post(REL_PATH, artist), MediaTypes.HAL_JSON)//
                .follow("self")
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resource<Artist>>() {});
        super.setAdded(new WrapChangedEntity<>(resource, resource));
        return resource;
    }

    public void saveGenre(Resource<? extends Entity> resource, int idGenre) {        
        requestService.postAbs(resource.getId().getHref() + "/genres/" + idGenre);
    }   
    
    public void deleteGenre(Resource<? extends Entity> resource, int idGenre) {
        requestService.deleteAbs(resource.getId().getHref() + "/genres/" + idGenre);
    }
    
    public void update(Resource<? extends Entity> resource) {       
        try {
            Resource<Artist> oldResource = new Traverson(new URI(resource.getId().getHref()), MediaTypes.HAL_JSON)//
                    .follow("self")
                    .withHeaders(sessionManager.createSessionHeaders())
                    .toObject(new ParameterizedTypeReference<Resource<Artist>>() {});
            requestService.put(resource);
            super.setUpdated(new WrapChangedEntity<>(oldResource, (Resource<Artist>)resource));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }       
    }
    
    public boolean exist(String search)  throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("search", search);       
        try {
        Resource<Artist> artistResource = new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH, "search", "by_name")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())    
                .toObject(new ParameterizedTypeReference<Resource<Artist>>() {});
        }
        catch (HttpClientErrorException ex) {
            return false;
        }
        return true;             
    }
    
    public PagedResources<Resource<Artist>> searchByNameAndRating(Paginator paginator, int minRating, int maxRating, String search) throws URISyntaxException {            
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("minrating", minRating);
        parameters.put("maxrating", maxRating);
        parameters.put("search", search);
        parameters.putAll(paginator.getParameters());
        
        PagedResources<Resource<Artist>> resource = new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH, "search", "by_name_and_rating")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.PagedResourcesType<Resource<Artist>>() {}); 
        
        paginator.setTotalElements((int) resource.getMetadata().getTotalElements());
        //logger.info("Content: {}", resource.getContent());
        //logger.info("Metadata: {}", resource.getMetadata());
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
                .follow(REL_PATH, "search", "by_genre_and_rating_and_name")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.PagedResourcesType<Resource<Artist>>() {}); 
        
        paginator.setTotalElements((int) resource.getMetadata().getTotalElements());
        //logger.info("Content: {}", resource.getContent());
        //logger.info("Metadata: {}", resource.getMetadata());
        return resource;       
    } 
}
