
package ru.javafx.musicbook.client.repository.impl;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianGenre;
import ru.javafx.musicbook.client.repository.MusicianGenreRepository;

@Repository
public class MusicianGenreRepositoryImpl extends CrudRepositoryImpl<MusicianGenre> implements MusicianGenreRepository {

    public MusicianGenreRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<MusicianGenre>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<MusicianGenre>>>() {};   
    }
    
    @Override
    public Resource<MusicianGenre> findByMusicianAndGenre(Resource<Musician> musician, Resource<Genre> genre) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        parameters.put("genre", genre.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByMusicianAndGenre"});
    }
  
}
