
package ru.javafx.musicbook.client.repository.impl;

import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Song;
import ru.javafx.musicbook.client.repository.SongRepository;

@Repository
public class SongRepositoryImpl extends CrudRepositoryImpl<Song> implements SongRepository {

}
