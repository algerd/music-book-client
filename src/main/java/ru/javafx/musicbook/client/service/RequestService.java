
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
   
    public int extractId(String href) {
        return Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));
    }
    
    public URI post(String rel, Entity entity) {
        try { 
            URI uri = new URI(basePath + rel);           
            HttpEntity<Entity> request = new HttpEntity<>(entity, sessionManager.createSessionHeaders());
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.exchange(uri, HttpMethod.POST, request, String.class).getHeaders().getLocation();
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }
    
    public void postAbs(String absRef, Entity entity) {
        try {          
            HttpEntity<Entity> request = new HttpEntity<>(entity, sessionManager.createSessionHeaders());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(new URI(absRef), HttpMethod.POST, request, String.class);
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    
    /*
    public void post(String rel, Entity entity) {
        try { 
            URI uri = new URI(basePath + rel);
            HttpEntity<Entity> request = new HttpEntity<>(entity, sessionManager.createSessionHeaders());
            new RestTemplate().postForObject(uri, request, String.class);
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }   
    */
    public void put(Resource<? extends Entity> resource) {    
        try { 
            URI uri = new URI(resource.getLink("self").getHref());           
            HttpEntity request = new HttpEntity(resource.getContent(), sessionManager.createSessionHeaders());
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
            HttpEntity request = new HttpEntity(sessionManager.createSessionHeaders());
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
