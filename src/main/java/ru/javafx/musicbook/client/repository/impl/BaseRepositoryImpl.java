
package ru.javafx.musicbook.client.repository.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import ru.javafx.musicbook.client.SessionManager;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.RelPath;
import ru.javafx.musicbook.client.repository.BaseRepository;

public abstract class BaseRepositoryImpl<T extends Entity> implements BaseRepository<T> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected String relPath;   
    private Class<T> entityType;
    
    @Autowired
    protected SessionManager sessionManager;
    
    @Value("${spring.data.rest.basePath}")
    protected String basePath;
      
    public BaseRepositoryImpl() {
        super();
        setEntityType();
        setRelPath();
    }
       
    private void setEntityType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        while (!(genericSuperclass instanceof ParameterizedType)) {
            genericSuperclass = ((Class) genericSuperclass).getGenericSuperclass();
        }
        Type[] arguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        entityType = (Class<T>) arguments[0];
    }
    
    private void setRelPath() {
        RelPath annotation = entityType.getAnnotation(RelPath.class);
        relPath = (annotation == null || annotation.value().equals("")) ?
            entityType.getSimpleName().toLowerCase() :
            annotation.value();   
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

    public void postAbs(String absRef) {
        try {          
            new RestTemplate().exchange(new URI(absRef), HttpMethod.POST, new HttpEntity(sessionManager.createSessionHeaders()), String.class);
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    public void deleteAbs(String absRef) {
        try {          
            new RestTemplate().exchange(new URI(absRef), HttpMethod.DELETE, new HttpEntity(sessionManager.createSessionHeaders()), String.class);
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
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

}
