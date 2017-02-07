
package ru.javafx.musicbook.client.repository.impl;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.MusicianGenre;
import ru.javafx.musicbook.client.repository.MusicianGenreRepository;

@Repository
public class MusicianGenreRepositoryImpl extends CrudRepositoryImpl<MusicianGenre> implements MusicianGenreRepository {

    public MusicianGenreRepositoryImpl() {
        resourceParameterizedType = new ParameterizedTypeReference<Resource<MusicianGenre>>() {};
        resourcesParameterizedType = new ParameterizedTypeReference<Resources<Resource<MusicianGenre>>>() {};   
    }
  
}
