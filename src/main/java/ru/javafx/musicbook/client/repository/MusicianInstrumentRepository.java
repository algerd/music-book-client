
package ru.javafx.musicbook.client.repository;

import java.net.URISyntaxException;
import org.springframework.hateoas.Resource;
import ru.javafx.musicbook.client.entity.Instrument;
import ru.javafx.musicbook.client.entity.Musician;
import ru.javafx.musicbook.client.entity.MusicianInstrument;

public interface MusicianInstrumentRepository extends CrudRepository<MusicianInstrument> {
    
    Resource<MusicianInstrument> findByMusicianAndInstrument(Resource<Musician> artist, Resource<Instrument> instrument) throws URISyntaxException;
    
}
