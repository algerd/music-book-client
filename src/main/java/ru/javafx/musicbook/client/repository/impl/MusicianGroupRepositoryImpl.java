
package ru.javafx.musicbook.client.repository.impl;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.MusicianGroup;
import ru.javafx.musicbook.client.repository.MusicianGroupRepository;

@Repository
public class MusicianGroupRepositoryImpl extends CrudRepositoryImpl<MusicianGroup> implements MusicianGroupRepository {

    public MusicianGroupRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<MusicianGroup>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<MusicianGroup>>>() {};   
    }
  
}
