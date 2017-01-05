
package ru.javafx.musicbook.client.repository.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.javafx.musicbook.client.SessionManager;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.RelPath;
import ru.javafx.musicbook.client.repository.BaseRepository;
import ru.javafx.musicbook.client.service.RequestService;

public abstract class BaseRepositoryImpl<T extends Entity> implements BaseRepository<T> {
    
    @Autowired
    protected RequestService requestService;
    
    @Autowired
    protected SessionManager sessionManager;
    
    @Value("${spring.data.rest.basePath}")
    protected String basePath;
    protected String relPath;
    
    private Class<T> entityType;
   
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
