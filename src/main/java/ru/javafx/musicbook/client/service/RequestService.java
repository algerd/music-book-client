
package ru.javafx.musicbook.client.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.hateoas.mvc.TypeReferences.ResourceType;
import org.springframework.hateoas.mvc.TypeReferences.ResourcesType;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.javafx.musicbook.client.SessionManager;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.Entity;

@Service
@SuppressWarnings("unchecked")
public class RequestService {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private SessionManager sessionManager;
    
    @Value("${spring.data.rest.basePath}")
    private String basePath;
    /*
    PageRequest page1 = new PageRequest(0, 20, Direction.ASC, "lastName", "salary");
    PageRequest page2 = new PageRequest(0, 20, new Sort(
        new Order(Direction.ASC, "lastName"), 
        new Order(Direction.DESC, "salary")
    );
     
    Transform PageRequest object to string :
        ?page=1&size=20&sort=id,asc&sort=name,desc
    */
    /*
    public String parsePageRequest(PageRequest pageRequest) {
        String strPageRequest = "?" 
            + "page=" + pageRequest.getPageNumber()
            + "&size=" + pageRequest.getPageSize();
        
        Iterator<Sort.Order> sortIterator = pageRequest.getSort().iterator();
        while (sortIterator.hasNext()) {
            Sort.Order order = sortIterator.next();
            strPageRequest += "&"
                + "sort=" + order.getProperty()
                + "," + ((order.getDirection().equals(Sort.Direction.DESC)) ? "desc" : "asc");
        }              
        return strPageRequest;
    }
    */
    /*
    public void post(String rel, Entity entity, Class<? extends Entity> responseType) {
        try { 
            URI uri = new URI(basePath + rel);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());            
            HttpEntity<IdAware> request = new HttpEntity<>(entity, headers);
            RestTemplate restTemplate = new RestTemplate();
            entity.setId(restTemplate.postForObject(uri, request, responseType).getId());
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    */
    /*
    public void put(String rel, Entity entity) {
       
        try { 
            URI uri = new URI(basePath + rel + "/" + entity.getId());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());            
            HttpEntity request = new HttpEntity(entity, headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.put(uri, request); 
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }       
    }
    */
    
    public void post(String rel, Entity entity, Class<? extends Entity> responseType) {
        try { 
            URI uri = new URI(basePath + rel);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());            
            HttpEntity<Entity> request = new HttpEntity<>(entity, headers);
            RestTemplate restTemplate = new RestTemplate();
            //System.out.println(restTemplate.postForObject(uri, request, responseType));
            URI entityUri = restTemplate.exchange(uri, HttpMethod.POST, request, String.class).getHeaders().getLocation();

            /*
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaTypes.HAL_JSON);
            headers.setAccept(mediaTypes);
            System.out.println(restTemplate.exchange(entityUri, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<Resource<Artist>>() {}).getBody());
            */
            /*
            RequestEntity<Void> req = RequestEntity.get(entityUri).header(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie()).accept(HAL_JSON).build();
            Resource<Artist> stores = (new RestTemplate()).exchange(req, new ResourceType<Artist>() {}).getBody();
            System.out.println(stores);
            */
           /*
            Map<String, Object> parameters = new HashMap<>();
            Resource<Artist> resource = new Traverson(uri, MediaTypes.HAL_JSON).
                    follow("artists")
                    .withHeaders(headers)
                    .toObject(new ParameterizedTypeReference<Resource<Artist>>() {});
            
            System.out.println(resource);
           */
            /*
            Traverson traverson = new Traverson(new URI(basePath), MediaTypes.HAL_JSON);
            Traverson.TraversalBuilder builder = traverson. //
				follow("artists").
                withHeaders(headers);
            logger.info("Discovered link: {}", builder.asTemplatedLink());
            */
            
            RequestEntity<Void> req = RequestEntity.get(entityUri).header(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie()).accept(HAL_JSON).build();
            Resource<Artist> store = (new RestTemplate()).exchange(req, new ParameterizedTypeReference<Resource<Artist>>() {}).getBody();
            System.out.println(store);
            
            //System.out.println(restTemplate.exchange(uri, HttpMethod.POST, request, String.class).getHeaders().getLocation());          
            
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    public void put(String rel, Resource<? extends Entity> resource) {    
        try { 
            URI uri = new URI(resource.getLink("self").getHref());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());            
            HttpEntity request = new HttpEntity(resource.getContent(), headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.put(uri, request); 
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }       
    }
    
    public void delete(String rel, int id) {
        try {
            URI uri = new URI(basePath + rel + "/" + id);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());
            HttpEntity request = new HttpEntity(headers);
            RestTemplate restTemplate = new RestTemplate();         
            restTemplate.exchange(uri, HttpMethod.DELETE, request, Object.class);
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    
}
