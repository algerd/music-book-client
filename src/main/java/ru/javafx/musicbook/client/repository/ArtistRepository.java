
package ru.javafx.musicbook.client.repository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.SessionManager;
import ru.javafx.musicbook.client.entity.Artist;

@Repository
public class ArtistRepository {
    
    private static final String REL_PATH = "artists";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private SessionManager sessionManager;
    
    @Value("${spring.data.rest.basePath}")
    private String basePath;  
    
    public PagedResources<Resource<Artist>> getArtists() throws URISyntaxException {    

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionManager.getSessionIdCookie());

        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(REL_PATH)
                .withHeaders(headers)
                .toObject(new TypeReferences.PagedResourcesType<Resource<Artist>>() {});                    
    }

}
