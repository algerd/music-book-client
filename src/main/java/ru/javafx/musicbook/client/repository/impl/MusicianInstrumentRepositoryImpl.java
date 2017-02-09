
package ru.javafx.musicbook.client.repository.impl;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianInstrument;
import ru.javafx.musicbook.client.repository.MusicianInstrumentRepository;

@Repository
public class MusicianInstrumentRepositoryImpl extends CrudRepositoryImpl<MusicianInstrument> implements MusicianInstrumentRepository {

    public MusicianInstrumentRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<MusicianInstrument>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<MusicianInstrument>>>() {};   
    }
    
    @Override
    public Resource<MusicianInstrument> findByMusicianAndInstrument(Resource<Musician> musician, Resource<Instrument> instrument) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("musician", musician.getId().getHref());
        parameters.put("instrument", instrument.getId().getHref());
        return getParameterizedResource(parameters, new String[]{relPath, "search", "findByMusicianAndInstrument"});
    }
  
}
