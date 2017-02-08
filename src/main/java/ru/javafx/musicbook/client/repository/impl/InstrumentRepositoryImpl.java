
package ru.javafx.musicbook.client.repository.impl;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import org.springframework.core.ParameterizedTypeReference;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.repository.InstrumentRepository;

@Repository
public class InstrumentRepositoryImpl extends CrudRepositoryImpl<Instrument> implements InstrumentRepository {

    public InstrumentRepositoryImpl() {    
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Instrument>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Instrument>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Instrument>>() {};
    }
   
}
