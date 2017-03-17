
package ru.javafx.musicbook.client.repository.impl;

import ru.javafx.musicbook.client.datacore.impl.CrudRepositoryImpl;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.repository.MusicianRepository;

@Repository
public class MusicianRepositoryImpl extends CrudRepositoryImpl<Musician> implements MusicianRepository {
    
    public MusicianRepositoryImpl() {  
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Musician>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Musician>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Musician>>() {};
    }
              
    @Override
    public Resources<Resource<Musician>> findByInstrument(Resource<Instrument> resource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("instrument", resource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_instrument"});       
    } 
    
    @Override
    public Resources<Resource<Musician>> findByGenre(Resource<Genre> resource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("genre", resource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_genre"});       
    }
    
    @Override
    public Resources<Resource<Musician>> findAllNames() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("projection", "get_name"); 
        parameters.put("sort", "name,asc"); 
        return getParameterizedResources(parameters);
    }
    
}
