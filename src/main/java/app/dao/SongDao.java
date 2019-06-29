package app.dao;

import app.model.Song;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SongDao extends JpaRepository<Song, Integer> {
   Optional<Song> findByReference(String reference);

   Song findByIdContaining(int id);

   List<Song> findByTitleContaining(String title);
}
