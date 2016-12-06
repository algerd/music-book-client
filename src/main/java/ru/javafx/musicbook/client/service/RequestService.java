
package ru.javafx.musicbook.client.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.javafx.musicbook.client.SessionManager;
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
    public URI post(String rel, Entity entity) {
        try { 
            URI uri = new URI(basePath + rel);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());            
            HttpEntity<Entity> request = new HttpEntity<>(entity, headers);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.exchange(uri, HttpMethod.POST, request, String.class).getHeaders().getLocation();
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }
    */
    
    public void post(String rel, Entity entity) {
        try { 
            URI uri = new URI(basePath + rel);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());            
            HttpEntity<Entity> request = new HttpEntity<>(entity, headers);
            new RestTemplate().postForObject(uri, request, String.class);
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }   
    
    public void put(Resource<? extends Entity> resource) {    
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
    
    public void delete(Resource<? extends Entity> resource) {
        try {
            URI uri = new URI(resource.getLink("self").getHref());
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
    
    public void deleteWithAlert(Resource<? extends Entity> resource) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the entity ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            delete(resource);
        }
    }
       
}
