package app.dao;

import app.model.Playlist;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistDao extends JpaRepository<Playlist, Integer> {
   List<Playlist> findByNameLikeAndGenreLike(final String name, final String genre);
}
