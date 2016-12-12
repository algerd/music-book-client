
package ru.javafx.musicbook.client.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.SessionManager;
import ru.javafx.musicbook.client.entity.Entity;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.service.RequestService;

@Repository
public class GenreRepository {

    private static final String REL_PATH = "genres";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Value("${spring.data.rest.basePath}")
    private String basePath;
    
    public void add(Genre genre) {
        requestService.post(REL_PATH, genre);
    }
    
    public void update(Resource<? extends Entity> resource) {
        requestService.put(resource);
    }
}
