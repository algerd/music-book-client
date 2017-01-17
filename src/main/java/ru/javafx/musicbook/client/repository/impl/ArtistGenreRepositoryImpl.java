
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
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.ArtistGenre;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.ArtistGenreRepository;

@Repository
public class ArtistGenreRepositoryImpl extends CrudRepositoryImpl<ArtistGenre> implements ArtistGenreRepository {

    @Override
    public Resource<ArtistGenre> findByArtistAndGenre(Resource<Artist> artist, Resource<Genre> genre) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("artist", artist.getId().getHref());
        parameters.put("genre", genre.getId().getHref());
        return new Traverson(new URI(basePath), MediaTypes.HAL_JSON)
                .follow(relPath, "search", "findByArtistAndGenre")
                .withTemplateParameters(parameters)
                .withHeaders(sessionManager.createSessionHeaders())
                .toObject(new ParameterizedTypeReference<Resource<ArtistGenre>>() {});  
    }
 
}
