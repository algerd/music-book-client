
package ru.javafx.musicbook.client.repository;

import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.ArtistGenre;
import ru.javafx.musicbook.client.repository.impl.CrudRepositoryImpl;

@Repository
public class ArtistGenreRepository extends CrudRepositoryImpl<ArtistGenre> {
    
}
