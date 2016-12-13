
package ru.javafx.musicbook.client.repository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
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
import ru.javafx.musicbook.client.controller.paginator.Paginator;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.service.RequestService;

@Repository
public class GenreRepository {

    private static final String REL_PATH = "genres";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Value("${spring.data.rest.basePath}")
    private String basePath;
    
    public void add(Genre genre) {
        requestService.post(REL_PATH, genre);
    }
    
    public void update(Resource<? extends Entity> resource) {
        requestService.put(resource);
    }
    
    public PagedResources<Resource<Genre>> get(Paginator paginator, String search) throws URISyntaxException {    
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("search", search);
        parameters.putAll(paginator.getParameters());
        
        PagedResources<Resource<Genre>> resource = new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH, "search", "by_name")
                .withTemplateParameters(parameters)
                .withHeaders(headers)
                .toObject(new TypeReferences.PagedResourcesType<Resource<Genre>>() {}); 
        
        paginator.setTotalElements((int) resource.getMetadata().getTotalElements());
        //logger.info("Content: {}", resource.getContent());
        //logger.info("Metadata: {}", resource.getMetadata());
        return resource;       
    }
}
