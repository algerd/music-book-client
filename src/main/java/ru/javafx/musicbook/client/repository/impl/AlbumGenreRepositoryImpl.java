
package ru.javafx.musicbook.client.repository.impl;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.entity.AlbumGenre;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.repository.AlbumGenreRepository;

@Repository
public class AlbumGenreRepositoryImpl extends CrudRepositoryImpl<AlbumGenre> implements AlbumGenreRepository {

    public AlbumGenreRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<AlbumGenre>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<AlbumGenre>>>() {};   
    }
        
    @Override
    public long countAlbumsByGenre(Resource<Genre> genreResource) throws URISyntaxException {
        Resources<Resource<AlbumGenre>> resources = getResources(genreResource.getId().getHref(), "albumGenres");          
        long i = 0;
        Iterator iterator = resources.iterator();
        for (; iterator.hasNext() ; ++i ) iterator.next();      
        return i;
    } 
        
    @Override
    public Resource<AlbumGenre> findByAlbumAndGenre(Resource<Album> album, Resource<Genre> genre) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("album", album.getId().getHref());
        parameters.put("genre", genre.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByAlbumAndGenre"});
    }
    
    ///////////////////////////
    /*
    @Override
    public Resources<Resource<Album>> findAlbumsByGenre(Resource<Genre> genreResource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("genre", genreResource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_genre"});       
    }
    */
}
