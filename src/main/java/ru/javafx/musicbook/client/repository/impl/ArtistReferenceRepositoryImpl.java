
package ru.javafx.musicbook.client.repository.impl;

import ru.javafx.musicbook.client.datacore.impl.CrudRepositoryImpl;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import org.springframework.core.ParameterizedTypeReference;
import ru.javafx.musicbook.client.entity.ArtistReference;
import ru.javafx.musicbook.client.repository.ArtistReferenceRepository;

@Repository
public class ArtistReferenceRepositoryImpl extends CrudRepositoryImpl<ArtistReference> implements ArtistReferenceRepository {

    public ArtistReferenceRepositoryImpl() {    
        resourceParameterizedType = new ParameterizedTypeReference<Resource<ArtistReference>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<ArtistReference>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<ArtistReference>>() {};
    }

}
