
package ru.javafx.musicbook.client.repository.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
    
}
