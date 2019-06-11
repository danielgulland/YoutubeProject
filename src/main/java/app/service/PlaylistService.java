package app.service;

import app.dao.PlaylistDao;
import app.exception.ApiException;
import app.model.Playlist;
import app.validation.ValidationError;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {

   @Autowired
   private PlaylistDao playlistDao;

   /**
    * Service call for creating a new playlist.
    *
    * @param playlist contains Playlist information
    */
   public void createNewPlaylist(final Playlist playlist) {
      playlistDao.save(playlist);
   }

   /**
    * Service call to get a playlist by id.
    *
    * @param id playlist's id
    * @return Playlist found by the playlist's id
    * @throws ApiException if no playlist exists for the playlist's id
    */
   public Playlist getPlaylistById(final int id) {
      final Optional<Playlist> playlist = playlistDao.findById(id);

      if (playlist.isPresent()) {
         return playlist.get();
      }

      throw new ApiException("Playlist not found", ValidationError.NOT_FOUND, "playlist");
   }
}
