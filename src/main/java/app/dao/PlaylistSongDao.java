package app.dao;

import app.model.PlaylistSong;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistSongDao extends JpaRepository<PlaylistSong, Integer> {

}
