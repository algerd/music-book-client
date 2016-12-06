
package ru.javafx.musicbook.client.repository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.SessionManager;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.service.RequestService;
import ru.javafx.musicbook.client.utils.PageRequest;
import ru.javafx.musicbook.client.utils.Sort;

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
    /*
    public Resource<Artist> add(Artist artist) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());
        return new Traverson(requestService.post(REL_PATH, artist), MediaTypes.HAL_JSON).
                follow("self")
                .withHeaders(headers)
                .toObject(new ParameterizedTypeReference<Resource<Artist>>() {});
    }
    */
    public void add(Artist artist) {
        requestService.post(REL_PATH, artist);
    }
    
    public void update(Resource<? extends Entity> resource) {
        requestService.put(resource);
    }
    
    public PagedResources<Resource<Artist>> getArtists() throws URISyntaxException {    

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());

        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH)
                .withHeaders(headers)
                .toObject(new TypeReferences.PagedResourcesType<Resource<Artist>>() {});         
    }
    
    public PagedResources<Resource<Artist>> getArtists(Integer page, Integer size, String sort) throws URISyntaxException {    

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", page);
        parameters.put("size", size);
        if (sort != null || sort != "") {
            parameters.put("sort", sort);
        }
        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH)
                .withTemplateParameters(parameters)
                .withHeaders(headers)
                .toObject(new TypeReferences.PagedResourcesType<Resource<Artist>>() {});         
    }
    
    public PagedResources<Resource<Artist>> getArtists(PageRequest pageRequest) throws URISyntaxException {    

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());
 
        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH)
                .withTemplateParameters(pageRequest.getParameters())
                .withHeaders(headers)
                .toObject(new TypeReferences.PagedResourcesType<Resource<Artist>>() {});         
    }
    
    
    /*
    public PagedResources<Resource<Artist>> getArtists(PageRequest pageRequest) throws URISyntaxException { 
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());
        
        return new Traverson(new URI(basePath + requestService.parsePageRequest(pageRequest)), MediaTypes.HAL_JSON)
                .follow(REL_PATH)
                .withHeaders(headers)
                .toObject(new TypeReferences.PagedResourcesType<Resource<Artist>>() {});
    }
    */
}
