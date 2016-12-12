
package ru.javafx.musicbook.client.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.SessionManager;
import ru.javafx.musicbook.client.service.RequestService;

@Repository
public class GenrePepository {

    private static final String REL_PATH = "genres";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Value("${spring.data.rest.basePath}")
    private String basePath;
}
