
package ru.javafx.musicbook.client.repository.impl;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.repository.SongRepository;

@Repository
public class SongRepositoryImpl extends CrudRepositoryImpl<Song> implements SongRepository {
    
    public SongRepositoryImpl() {  
        resourceParameterizedType = new ParameterizedTypeReference<Resource<Song>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<Song>>>() {}; 
        pagedResourcesType = new TypeReferences.PagedResourcesType<Resource<Song>>() {};
    }

}
