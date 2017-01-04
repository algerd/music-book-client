
package ru.javafx.musicbook.client.repository.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.repository.CrudRepository;

public abstract class CrudRepositoryImpl<T extends Entity> extends ChangeRepositoryImpl<T> implements CrudRepository<T> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    public void deleteWithAlert(Resource<? extends Entity> resource) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the entity ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            delete(resource);
        }
    }
   
    public void delete(Resource<? extends Entity> resource) {
        try {
            URI uri = new URI(resource.getLink("self").getHref());
            HttpEntity request = new HttpEntity(sessionManager.createSessionHeaders());
            RestTemplate restTemplate = new RestTemplate();         
            restTemplate.exchange(uri, HttpMethod.DELETE, request, Object.class);
            super.setDeleted(new WrapChangedEntity<>((Resource<T>)resource, null));
        }  
        catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    public void update(Resource<? extends Entity> resource) {
        try {
            Resource<T> oldResource = new Traverson(new URI(resource.getId().getHref()), MediaTypes.HAL_JSON)//
                    .follow("self")
                    .withHeaders(sessionManager.createSessionHeaders())
                    .toObject(new ParameterizedTypeReference<Resource<T>>() {});
            requestService.put(resource);
            super.setUpdated(new WrapChangedEntity<>(oldResource, (Resource<T>)resource));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }  
    } 
    
}
