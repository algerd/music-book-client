
package ru.javafx.musicbook.client.repository.impl;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.entity.SongGenre;
import ru.javafx.musicbook.client.repository.SongGenreRepository;

@Repository
public class SongGenreRepositoryImpl extends CrudRepositoryImpl<SongGenre> implements SongGenreRepository {

    public SongGenreRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<SongGenre>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<SongGenre>>>() {};   
    }
        
    @Override
    public long countSongsByGenre(Resource<Genre> genreResource) throws URISyntaxException {
        Resources<Resource<SongGenre>> resources = getResources(genreResource.getId().getHref(), "songGenres");          
        long i = 0;
        Iterator iterator = resources.iterator();
        for (; iterator.hasNext() ; ++i ) iterator.next();      
        return i;
    } 
        
    @Override
    public Resource<SongGenre> findBySongAndGenre(Resource<Song> song, Resource<Genre> genre) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("song", song.getId().getHref());
        parameters.put("genre", genre.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findBySongAndGenre"});
    }
    
    @Override
    public long countBySongAndGenre(Resource<Song> song, Resource<Genre> genre) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("song", song.getId().getHref());
        parameters.put("genre", genre.getId().getHref());
        return countParameterizedQuery(parameters, new String[]{relPath, "search", "countBySongAndGenre"});
    }
    
}
