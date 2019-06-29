package app.service;

import app.dao.PlaylistDao;
import app.dao.PlaylistSongDao;
import app.dao.SongDao;
import app.exception.ApiException;
import app.model.Playlist;
import app.request.UpdatePlaylistData;

import app.model.PlaylistSong;
import app.model.Song;
import app.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static app.constant.FieldConstants.PLAYLIST;
import static app.constant.FieldConstants.PLAYLIST_ID;
import static app.constant.FieldConstants.SONG_ID;

@Service
public class PlaylistService {

   @Autowired
   private PlaylistDao playlistDao;

   @Autowired
   private SongDao songDao;

   @Autowired
   private PlaylistSongDao playlistSongDao;

   /**
    * Service call for creating a new playlist.
    *
    * @param playlist contains Playlist information
    */
   public void createNewPlaylist(final Playlist playlist) {
      playlistDao.save(playlist);
   }

   /**
    * Service call to add a song to a playlist.
    *
    * @param songId song's id
    * @param playlistId playlist's id
    * @throws ApiException if no playlist exists for the playlist's id
    * @throws ApiException if no song exists for the song's id
    */
   public void addSongToPlaylist(final int songId, final int playlistId) {
      final Optional<Song> song = songDao.findById(songId);
      final Optional<Playlist> playlist = playlistDao.findById(playlistId);

      if (!playlist.isPresent()) {
         throw new ApiException("Playlist not found", ValidationError.NOT_FOUND, PLAYLIST_ID);
      }

      if (!song.isPresent()) {
         throw new ApiException("Song not found", ValidationError.NOT_FOUND, SONG_ID);
      }

      playlistSongDao.save(PlaylistSong.builder().songId(songId).playlistId(playlistId).build());
   }

   /**
    * Service call to get a playlist by id.
    *
    * @param id playlist id to check for
    * @return Playlist found by the playlist's id
    * @throws ApiException if no playlist exists for the playlist's id
    */
   public Playlist getPlaylistById(final int id) {
      final Optional<Playlist> playlist = playlistDao.findById(id);

      if (playlist.isPresent()) {
         return playlist.get();
      }

      throw new ApiException("Playlist not found", ValidationError.NOT_FOUND, PLAYLIST);
   }

   /**
    * Get songs in a playlist.
    *
    * @param id Playlist's id
    * @return List of songs for a specific playlist
    */
   public List<Song> getSongsInPlaylist(final int id) {
      final List<PlaylistSong> playlistSongs = playlistSongDao.getPlaylistSong(id);

      if (!playlistSongs.isEmpty()) {
         final List<Song> songs = new ArrayList<>();
         for (PlaylistSong existingPlaylistSong : playlistSongs) {
            songs.add(songDao.findById(existingPlaylistSong.getSongId()).get());
         }

         return songs;
      }
      throw new ApiException("Playlist not found", ValidationError.NOT_FOUND, "playlist");
   }

   /**
    * Service call to update a playlist by id.
    *
    * @param id playlist id to check for
    * @param updatePlaylistData contains information to update a playlist
    */
   public void updatePlaylistById(final int id, final UpdatePlaylistData updatePlaylistData) {
      final Optional<Playlist> existingPlaylist = playlistDao.findById(id);

      if (!existingPlaylist.isPresent()) {
         throw new ApiException("Playlist does not exist", ValidationError.NOT_FOUND, PLAYLIST);
      }

      final Playlist playlist = existingPlaylist.get();

      if (StringUtils.isNotBlank(updatePlaylistData.getName())) {
         playlist.setName(updatePlaylistData.getName());
      }

      if (StringUtils.isNotBlank(updatePlaylistData.getGenre())) {
         playlist.setGenre(updatePlaylistData.getGenre());
      }

      if (updatePlaylistData.getIsPrivate() != null) {
         playlist.setPrivate(updatePlaylistData.getIsPrivate());
      }

      playlistDao.save(playlist);
   }

   /**
    * Service call to delete a playlist by id.
    *
    * @param id playlist's id
    * @throws ApiException if no playlist exists for the playlist's id
    */
   public void deletePlaylist(final int id) {
      final Optional<Playlist> playlist = playlistDao.findById(id);

      if (!playlist.isPresent()) {
         throw new ApiException("Playlist not found", ValidationError.NOT_FOUND, PLAYLIST_ID);
      }

      playlistDao.delete(playlist.get());
   }

   /** Service call to get playlists by name or genre.
    *
    * @param name name to filter by
    * @param genre genre to filter by
    * @return List of Playlists that match the name, genre, both, or returns all playlists
    */
   public List<Playlist> getPlaylistsByFilter(final String name, final String genre) {
      return playlistDao.findByNameLikeAndGenreLike(name, genre);
   }
}
