
package ru.javafx.musicbook.client.repository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.controller.paginator.Paginator;
import ru.javafx.musicbook.client.entity.ArtistGenre;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.impl.CrudRepositoryImpl;
import ru.javafx.musicbook.client.utils.Helper;

@Repository
public class GenreRepository extends CrudRepositoryImpl<Genre> {
    
    public long countArtistsByGenre(Resource<Genre> genreResource) throws URISyntaxException {
        Resources<Resource<ArtistGenre>> resources = new Traverson(new URI(genreResource.getId().getHref()), MediaTypes.HAL_JSON)//
                    .follow("artistGenres")
                    .withHeaders(sessionManager.createSessionHeaders())
                    .toObject(new TypeReferences.ResourcesType<Resource<ArtistGenre>>() {});      
        long i = 0;
        Iterator iterator = resources.iterator();
        for (; iterator.hasNext() ; ++i ) iterator.next();      
        return i;
    }

    public PagedResources<Resource<Genre>> searchByName(Paginator paginator, String search) throws URISyntaxException {    
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("search", search);
        parameters.putAll(paginator.getParameters());
        
        PagedResources<Resource<Genre>> resources = new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "by_name")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.PagedResourcesType<Resource<Genre>>() {}); 
        
        paginator.setTotalElements((int) resources.getMetadata().getTotalElements());
        return resources;       
    }
    
    public Resources<Resource<Genre>> findAll() throws URISyntaxException {
        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new TypeReferences.ResourcesType<Resource<Genre>>() {});       
    }
    
    public Resources<Resource<Genre>> findByArtist(Resource<? extends Entity> artistResource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id_artist", Helper.getId(artistResource));

        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "by_artist")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resources<Resource<Genre>>>() {});      
    } 

}
