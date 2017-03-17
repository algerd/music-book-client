
package ru.javafx.musicbook.client.repository.impl;

import ru.javafx.musicbook.client.datacore.impl.CrudRepositoryImpl;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import org.springframework.core.ParameterizedTypeReference;
import ru.javafx.musicbook.client.entity.Genre;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.repository.InstrumentRepository;

@Repository
public class InstrumentRepositoryImpl extends CrudRepositoryImpl<Instrument> implements InstrumentRepository {

    public InstrumentRepositoryImpl() {    
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Instrument>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Instrument>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Instrument>>() {};
    }
    
    @Override
    public Resources<Resource<Instrument>> findAllNames() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("projection", "get_name"); 
        parameters.put("sort", "name,asc"); 
        return getParameterizedResources(parameters);     
    }
    
    @Override
    public Resources<Resource<Instrument>> findByMusician(Resource<Musician> resource) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", resource.getId().getHref());
        return getParameterizedResources(parameters, new String[]{relPath, "search", "by_musician"});
    }
   
}
