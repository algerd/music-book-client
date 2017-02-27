
package ru.javafx.musicbook.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javafx.musicbook.client.repository.AlbumRepository;
import ru.javafx.musicbook.client.repository.ArtistRepository;
import ru.javafx.musicbook.client.repository.SongRepository;

@Service
public class RepositoryService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private SongRepository songRepository;

    public ArtistRepository getArtistRepository() {
        return artistRepository;
    }

    public AlbumRepository getAlbumRepository() {
        return albumRepository;
    }

    public SongRepository getSongRepository() {
        return songRepository;
    }

}
