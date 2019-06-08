package app.service;

import app.dao.PlaylistDao;
import app.model.Playlist;

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
}
