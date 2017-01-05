
package ru.javafx.musicbook.client.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.embed.swing.SwingFXUtils;
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
       
    public Image getImage(String url) {
        return new Image(url, true);
    }
    
    public HttpStatus postImage(Resource<? extends Entity> resource, Image image) {       
        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", baos);
            baos.flush();
            byte[] byteArray = baos.toByteArray();         
            baos.close();

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
    
    public void deleteImage(Resource<? extends Entity> resource) {
        try {  
            URI uri = new URI(resource.getLink("post_delete_image").getHref());
            new RestTemplate().exchange(uri, HttpMethod.DELETE, new HttpEntity(sessionManager.createSessionHeaders()), String.class);
        } catch (URISyntaxException ex) {
            logger.error(ex.getMessage());
            //ex.printStackTrace(); 
        }      
    }

    public int extractId(String href) {
        return Integer.valueOf(href.substring(href.lastIndexOf("/") + 1));
    }
      
}
