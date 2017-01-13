
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Album;

public interface AlbumRepository extends CrudRepository<Album> {
    
    PagedResources<Resource<Album>> searchByNameAndRatingAndYear(Map<String, Object> parameters) throws URISyntaxException;

    PagedResources<Resource<Album>> searchByNameAndRatingAndYearAndGenre(Map<String, Object> parameters) throws URISyntaxException;
    
}
