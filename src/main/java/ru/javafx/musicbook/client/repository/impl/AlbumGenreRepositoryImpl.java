
package ru.javafx.musicbook.client.repository.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.AlbumGenre;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.AlbumGenreRepository;

@Repository
public class AlbumGenreRepositoryImpl extends CrudRepositoryImpl<AlbumGenre> implements AlbumGenreRepository {

    @Override
    public Resource<AlbumGenre> findByAlbumAndGenre(Resource<Album> album, Resource<Genre> genre) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("album", album.getId().getHref());
        parameters.put("genre", genre.getId().getHref());
        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "findByAlbumAndGenre")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resource<AlbumGenre>>() {});     
    }
    
}
