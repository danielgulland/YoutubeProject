package app.dao;

import app.model.PlaylistSong;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistSongDao extends JpaRepository<PlaylistSong, Integer> {
   Optional<PlaylistSong> findByPlaylistIdAndSongId(final int songId, final int playlistId);
}
