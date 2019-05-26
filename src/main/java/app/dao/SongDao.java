package app.dao;

import app.model.Song;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SongDao extends JpaRepository<Song, Integer> {
   Optional<Song> findByReference(String reference);
}
