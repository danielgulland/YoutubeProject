package app.dao;

import app.model.Playlist;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistDao extends JpaRepository<Playlist, Integer> {

}
