
package ru.javafx.musicbook.client.repository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.SessionManager;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.service.RequestService;
import ru.javafx.musicbook.client.controller.paginator.Paginator;
import ru.javafx.musicbook.client.entity.ArtistGenre;
import ru.javafx.musicbook.client.entity.Genre;

@Repository
public class ArtistRepository {
    
    private static final String REL_PATH = "artists";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Value("${spring.data.rest.basePath}")
    private String basePath; 
    
    public void save(Artist artist) {
        requestService.post(REL_PATH, artist);
    }

    public Resource<Artist> saveAndGetResource(Artist artist) {
        return new Traverson(requestService.post(REL_PATH, artist), MediaTypes.HAL_JSON)//
                .follow("self")
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resource<Artist>>() {});      
    }

    public void saveGenre(Resource<? extends Entity> resource, int idGenre) {        
        requestService.postAbs(resource.getId().getHref() + "/genres/" + idGenre);
    }   
    
    public void update(Resource<? extends Entity> resource) {
        requestService.put(resource);
    }
    
    public PagedResources<Resource<Artist>> getArtists(Paginator paginator, int minRating, int maxRating, String search) throws URISyntaxException {            
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
    
    public List<Resource<Genre>> getGenres(Resource<? extends Entity> artistResource) throws URISyntaxException {
        List<Resource<Genre>> genreResources = new ArrayList<>();
        Resources<Resource<ArtistGenre>> artistGenreResources = new Traverson(new URI(artistResource.getId().getHref()), MediaTypes.HAL_JSON)
                .follow("artistGenres")
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resources<Resource<ArtistGenre>>>() {});
        
        artistGenreResources.getContent().parallelStream().forEach(artistGenre -> {
            try {
                Resource<Genre> genreResource = new Traverson(new URI(artistGenre.getId().getHref()), MediaTypes.HAL_JSON)
                        .follow("genre")
                        .withHeaders(sessionManager.createSessionHeaders())    
                        .toObject(new ParameterizedTypeReference<Resource<Genre>>() {});
                genreResources.add(genreResource);
            } catch (URISyntaxException ex) {
                //logger.error(ex.getMessage());
                ex.printStackTrace();
            }          
        });
        return genreResources;
    }
    
}
