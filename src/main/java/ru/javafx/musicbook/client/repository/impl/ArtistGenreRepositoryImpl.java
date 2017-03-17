
package ru.javafx.musicbook.client.repository.impl;

import ru.javafx.musicbook.client.datacore.impl.CrudRepositoryImpl;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Artist;
import ru.javafx.musicbook.client.entity.ArtistGenre;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.ArtistGenreRepository;

@Repository
public class ArtistGenreRepositoryImpl extends CrudRepositoryImpl<ArtistGenre> implements ArtistGenreRepository {
   
    public ArtistGenreRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<ArtistGenre>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<ArtistGenre>>>() {};   
    }
    
    @Override
    public long countArtistsByGenre(Resource<Genre> genreResource) throws URISyntaxException {
        Resources<Resource<ArtistGenre>> resources = getResources(genreResource.getId().getHref(), "artistGenres");          
        long i = 0;
        Iterator iterator = resources.iterator();
        for (; iterator.hasNext() ; ++i ) iterator.next();      
        return i;
    } 

    @Override
    public Resource<ArtistGenre> findByArtistAndGenre(Resource<Artist> artist, Resource<Genre> genre) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("artist", artist.getId().getHref());
        parameters.put("genre", genre.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByArtistAndGenre"});
    }
    
    @Override
    public long countByArtistAndGenre(Resource<Artist> artist, Resource<Genre> genre) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("artist", artist.getId().getHref());
        parameters.put("genre", genre.getId().getHref());
        return countParameterizedQuery(parameters, new String[]{relPath, "search", "countByArtistAndGenre"});
    }

}
