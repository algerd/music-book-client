
package ru.javafx.musicbook.client.repository.impl;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.repository.MusicianRepository;

@Repository
public class MusicianRepositoryImpl extends CrudRepositoryImpl<Musician> implements MusicianRepository {
    
    public MusicianRepositoryImpl() {  
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Musician>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Musician>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Musician>>() {};
    }
   
}
