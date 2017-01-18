
package ru.javafx.musicbook.client.repository.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.repository.CrudRepository;

@SuppressWarnings("unchecked")
public abstract class CrudRepositoryImpl<T extends Entity> extends ChangeRepositoryImpl<T> implements CrudRepository<T> {
    
    protected ParameterizedTypeReference<Resource<T>> resourceParameterizedType;
    protected ParameterizedTypeReference<Resources<Resource<T>>> resourcesParameterizedType; 
    protected TypeReferences.PagedResourcesType<Resource<T>> pagedResourcesType;
    
    @Override
    public URI save(String rel, T entity) throws URISyntaxException {
        URI uri = new URI(basePath + rel);           
        HttpEntity<Entity> request = new HttpEntity<>(entity, sessionManager.createSessionHeaders());
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(uri, HttpMethod.POST, request, String.class).getHeaders().getLocation();
    }
    
    @Override
    public URI save(T entity) throws URISyntaxException {
        return save(relPath, entity);
    }
    
    @Override
    public HttpStatus save(String absRef) throws URISyntaxException {          
        return new RestTemplate().exchange(new URI(absRef), HttpMethod.POST, new HttpEntity(sessionManager.createSessionHeaders()), String.class).getStatusCode();
    }
     
    @Override
    public Resource<T> update(Resource<T> resource) throws URISyntaxException {    
        URI uri = new URI(resource.getLink("self").getHref());           
        HttpEntity request = new HttpEntity(resource.getContent(), sessionManager.createSessionHeaders());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(uri, request); 
        return resource;
    }
    
    @Override
    public void delete(Resource<T> resource) throws URISyntaxException {
        URI uri = new URI(resource.getLink("self").getHref());
        HttpEntity request = new HttpEntity(sessionManager.createSessionHeaders());
        RestTemplate restTemplate = new RestTemplate();         
        restTemplate.exchange(uri, HttpMethod.DELETE, request, Object.class); 
    }
    
    @Override
    public HttpStatus delete(String absRef) throws URISyntaxException {        
        return new RestTemplate().exchange(new URI(absRef), HttpMethod.DELETE, new HttpEntity(sessionManager.createSessionHeaders()), String.class).getStatusCode();
    }
    
    @Override
    public void deleteWithAlert(Resource<? extends Entity> resource) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the entity ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                delete((Resource<T>)resource);
                super.setDeleted(new WrapChangedEntity<>((Resource<T>)resource, null));
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
                //ex.printStackTrace();
            }           
        }
    }
    
    @Override
    public boolean existByName(String search) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("search", search);         
        try {
            new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "exist_by_name")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())    
                .toObject(new ParameterizedTypeReference<Resource<T>>() {});
        }
        catch (HttpClientErrorException ex) {
            return false;
        }
        return true;             
    } 
    
    
    @Override
    public void saveImage(Resource<T> resource, Image image) {
        if (image != null) {
            postImage(resource, image);
        } else {
            deleteImage(resource);
        }        
    }
   
    @Override
    public HttpStatus postImage(Resource<T> resource, Image image) {       
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            byte[] byteArray;
            ImageIO.write(bImage, "jpg", baos);
            baos.flush();
            byteArray = baos.toByteArray();

            URI uri = new URI(resource.getLink("post_delete_image").getHref()); 
            HttpHeaders headers = sessionManager.createSessionHeaders(); 
            headers.setContentType(MediaType.IMAGE_JPEG);
            HttpEntity<byte[]> entity = new HttpEntity<>(byteArray, headers);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.exchange(uri, HttpMethod.POST, entity , String.class).getStatusCode();                     
        } catch (IOException | URISyntaxException ex) {
            logger.error(ex.getMessage());
            //ex.printStackTrace(); 
        }
        return null;
    }  
    
    @Override
    public void deleteImage(Resource<T> resource) {
        try {  
            URI uri = new URI(resource.getLink("post_delete_image").getHref());
            new RestTemplate().exchange(uri, HttpMethod.DELETE, new HttpEntity(sessionManager.createSessionHeaders()), String.class);
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }      
    }
       
    @Override
    public Resource<T> saveAndGetResource(T entity) throws URISyntaxException {
        return new Traverson(save(relPath, entity), MediaTypes.HAL_JSON)//
                .follow("self")
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(resourceParameterizedType);
    }
       
    ///////////////////////////////////////////////////////
    @Override
    public Resource<T> getResource(String path) throws URISyntaxException {       
        return new Traverson(new URI(path), MediaTypes.HAL_JSON)//
                .follow("self")
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(resourceParameterizedType);
    }
    
    @Override
    public Resources<Resource<T>> getResources() throws URISyntaxException {
        return getResources(basePath, relPath);
    }   
    @Override
    public Resources<Resource<T>> getResources(String[] rels) throws URISyntaxException {
        return getResources(basePath, rels);
    }    
    @Override
    public Resources<Resource<T>> getResources(String path, String... rels) throws URISyntaxException {
        return new Traverson(new URI(path), MediaTypes.HAL_JSON)
                .follow(rels)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(resourcesParameterizedType);       
    }
    
    @Override
    public Resource<T> getParameterizedResource(Map<String, Object> parameters, String... rels) throws URISyntaxException {
        return getParameterizedResource(basePath, parameters, rels);
    } 
    @Override
    public Resource<T> getParameterizedResource(String path, Map<String, Object> parameters, String... rels) throws URISyntaxException {
        return new Traverson(new URI(path), MediaTypes.HAL_JSON)
                .follow(rels)
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(resourceParameterizedType);  
    } 
        
    @Override
    public Resources<Resource<T>> getParameterizedResources(Map<String, Object> parameters) throws URISyntaxException {
        return getParameterizedResources(basePath, parameters, relPath);
    }    
    @Override
    public Resources<Resource<T>> getParameterizedResources(Map<String, Object> parameters, String... rels) throws URISyntaxException {
        return getParameterizedResources(basePath, parameters, rels);
    }    
    @Override
    public Resources<Resource<T>> getParameterizedResources(String path, Map<String, Object> parameters, String... rels) throws URISyntaxException {
        return new Traverson(new URI(path), MediaTypes.HAL_JSON)
                .follow(rels)
                .withHeaders(sessionManager.createSessionHeaders())
                .withTemplateParameters(parameters)
                .toObject(resourcesParameterizedType);       
    }
    
    
    public PagedResources<Resource<T>> getPagedResources(Map<String, Object> parameters, String... rels) throws URISyntaxException {
        return getPagedResources(basePath, parameters, rels);
    }
    public PagedResources<Resource<T>> getPagedResources(String path, Map<String, Object> parameters, String... rels) throws URISyntaxException {            
        return new Traverson(new URI(path), MediaTypes.HAL_JSON)
                .follow(rels)
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(pagedResourcesType);       
    }
    
    
    
        
}
