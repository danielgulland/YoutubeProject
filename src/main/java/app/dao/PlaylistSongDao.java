package app.dao;

import app.model.PlaylistSong;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistSongDao extends JpaRepository<PlaylistSong, Integer> {
   @Query(value = "SELECT * FROM playlist_song WHERE playlist_id = :playlistId", nativeQuery = true)
   List<PlaylistSong> getPlaylistSong(@Param("playlistId") int playlistId);
}
