
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
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.controller.paginator.Paginator;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.impl.CrudRepositoryImpl;
import ru.javafx.musicbook.client.repository.impl.WrapChangedEntity;
import ru.javafx.musicbook.client.utils.Helper;

@Repository
public class GenreRepository extends CrudRepositoryImpl<Genre> {

    private static final String REL_PATH = "genres";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /*   
    public void add(Genre genre) {
        requestService.post(REL_PATH, genre);
    }
    */
    public Resource<Genre> saveAndGetResource(Genre genre) {
        Resource<Genre> resource = new Traverson(requestService.post(REL_PATH, genre), MediaTypes.HAL_JSON)//
                .follow("self")
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resource<Genre>>() {});
        super.setAdded(new WrapChangedEntity<>(resource, resource));
        return resource;
    }
    
    public void update(Resource<? extends Entity> resource) {
        //requestService.put(resource);
        try {
            Resource<Genre> oldResource = new Traverson(new URI(resource.getId().getHref()), MediaTypes.HAL_JSON)//
                    .follow("self")
                    .withHeaders(sessionManager.createSessionHeaders())
                    .toObject(new ParameterizedTypeReference<Resource<Genre>>() {});
            requestService.put(resource);
            super.setUpdated(new WrapChangedEntity<>(oldResource, (Resource<Genre>)resource));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }  
    } 
    
    public PagedResources<Resource<Genre>> searchByName(Paginator paginator, String search) throws URISyntaxException {    
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("search", search);
        parameters.putAll(paginator.getParameters());
        
        PagedResources<Resource<Genre>> resources = new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH, "search", "by_name")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.PagedResourcesType<Resource<Genre>>() {}); 
        
        paginator.setTotalElements((int) resources.getMetadata().getTotalElements());
        return resources;       
    }
    
    public Resources<Resource<Genre>> findAll() throws URISyntaxException {
        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.ResourcesType<Resource<Genre>>() {});       
    }
    
    public Resources<Resource<Genre>> findByArtist(Resource<? extends Entity> artistResource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id_artist", Helper.getId(artistResource));

        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH, "search", "by_artist")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resources<Resource<Genre>>>() {});      
    } 
}
