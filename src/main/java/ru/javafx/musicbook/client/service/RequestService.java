
package ru.javafx.musicbook.client.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.javafx.musicbook.client.entity.IdAware;
import ru.javafx.musicbook.client.SessionManager;

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
    public void post(String rel, IdAware entity, Class<? extends IdAware> responseType) {
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
    
    public void put(String rel, IdAware entity) {
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
