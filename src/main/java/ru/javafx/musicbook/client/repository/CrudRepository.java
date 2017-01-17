
package ru.javafx.musicbook.client.repository;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.scene.image.Image;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import ru.javafx.musicbook.client.entity.Entity;

public interface CrudRepository<T extends Entity> extends ChangeRepository<T> {
    
    URI save(String rel, T entity) throws URISyntaxException;
    
    URI save(T entity) throws URISyntaxException;
    
    HttpStatus save(String absRef) throws URISyntaxException;
        
    Resource<T> update(Resource<T> resource) throws URISyntaxException;
    
    void delete(Resource<T> resource) throws URISyntaxException;
    
    HttpStatus delete(String absRef) throws URISyntaxException;
    
    void deleteWithAlert(Resource<? extends Entity> resource);
    
    boolean existByName(String search)  throws URISyntaxException;
    
    void saveImage(Resource<T> resource, Image image);
    
    HttpStatus postImage(Resource<T> resource, Image image);
    
    void deleteImage(Resource<T> resource);
    
    //Resource<T> saveAndGetResource(T entity) throws URISyntaxException;
    
    //Resource<T> getResource(String link) throws URISyntaxException;
    
}
