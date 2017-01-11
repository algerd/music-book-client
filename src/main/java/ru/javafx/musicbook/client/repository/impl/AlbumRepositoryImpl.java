
package ru.javafx.musicbook.client.repository.impl;

import org.springframework.stereotype.Repository;
import ru.javafx.musicbook.client.entity.Album;
import ru.javafx.musicbook.client.repository.AlbumRepository;

@Repository
public class AlbumRepositoryImpl extends CrudRepositoryImpl<Album> implements AlbumRepository {
    
}
