
package ru.javafx.musicbook.client.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    
    public Image getImage(String url) {
        return new Image(url, true);
    }
    
    public HttpStatus postImage(Resource<? extends Entity> resource, Image image, String imageFormat) {       
        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bImage, imageFormat, baos);
            baos.flush();
            byte[] byteArray = baos.toByteArray();         
            baos.close();

            URI uri = new URI(resource.getId().getHref() + "/image");  
            HttpHeaders headers = sessionManager.createSessionHeaders(); 
            imageFormat = imageFormat.toLowerCase();
            MediaType contentType;
            if (imageFormat.equals("jpg") || imageFormat.equals("jpeg")) {
                contentType = MediaType.IMAGE_JPEG;
            } else if (imageFormat.equals("png")) {
                contentType = MediaType.IMAGE_PNG;
            } else {
                return null;
            }
            headers.setContentType(contentType);
            HttpEntity<byte[]> entity = new HttpEntity<>(byteArray, headers);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.exchange(uri, HttpMethod.POST, entity , String.class).getStatusCode();                     
        } catch (IOException | URISyntaxException ex) {
            logger.error(ex.getMessage());
            //ex.printStackTrace(); 
        }
        return null;
    }  
    
    public void deleteImage(Resource<? extends Entity> resource) {
        try {  
            URI uri = new URI(resource.getId().getHref() + "/image"); 
            new RestTemplate().exchange(uri, HttpMethod.DELETE, new HttpEntity(sessionManager.createSessionHeaders()), String.class);
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
            //ex.printStackTrace(); 
        }      
    }
    
    public void getImage(Resource<? extends Entity> resource) {
        try {  
            
            URI uri = new URI(resource.getId().getHref() + "/image"); 
            
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
            //ex.printStackTrace(); 
        }      
    }
   
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
