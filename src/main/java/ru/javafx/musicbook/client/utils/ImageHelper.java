
package ru.javafx.musicbook.client.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.javafx.musicbook.client.SessionManager;

@Service
public class ImageHelper {
    
    private final Logger logger = LogManager.getLogger(ImageUtil.class);
    
    @Autowired
    private SessionManager sessionManager;
    
    public String writeImage2(Image image, String imageFormat) {
        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bImage, imageFormat, baos);
            baos.flush();
            String encodedImage = (new Base64(false)).encodeToString(baos.toByteArray());
            baos.close();
            return java.net.URLEncoder.encode(encodedImage, "ISO-8859-1");          
        } 
        catch (IOException e) {
            logger.error("Error: ", e);
            //e.printStackTrace();
        }
        return null;
    }
    
    public HttpStatus postImage(String ref, Image image, String imageFormat) {
        try {             
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bImage, imageFormat, baos);
            baos.flush();
            String encodedImage = (new Base64(false)).encodeToString(baos.toByteArray());
            baos.close();
            encodedImage = java.net.URLEncoder.encode(encodedImage, "ISO-8859-1");  
                      
            URI uri = new URI(ref);  
            HttpHeaders headers = sessionManager.createSessionHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);     
            HttpEntity<String> request = new HttpEntity<>(encodedImage, headers);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.exchange(uri, HttpMethod.POST, request, String.class).getStatusCode();
        }  
        catch (IOException | URISyntaxException ex) {
            logger.error(ex.getMessage());
            //ex.printStackTrace(); 
        }
        return null;
    }

}
