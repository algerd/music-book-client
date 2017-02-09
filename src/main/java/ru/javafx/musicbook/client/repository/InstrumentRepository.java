
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;


public interface InstrumentRepository extends CrudRepository<Instrument>  {
    
    Resources<Resource<Instrument>> findAllNames() throws URISyntaxException;
    
    Resources<Resource<Instrument>> findByMusician(Resource<Musician> resource) throws URISyntaxException;
    
}
