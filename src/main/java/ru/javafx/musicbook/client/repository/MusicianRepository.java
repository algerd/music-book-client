
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;

public interface MusicianRepository extends CrudRepository<Musician> {
    
    Resources<Resource<Musician>> findByInstrument(Resource<Instrument> resource) throws URISyntaxException;
    
}
