package app.service;

import app.dao.PlaylistDao;
import app.exception.ApiException;
import app.model.Playlist;
import app.request.UpdatePlaylistData;
import app.validation.ValidationError;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static app.constant.FieldConstants.PLAYLIST;

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

      if (StringUtils.isNotBlank(updatePlaylistData.getName())) {
         existingPlaylist.get().setName(updatePlaylistData.getName());
      }

      if (StringUtils.isNotBlank(updatePlaylistData.getGenre())) {
         existingPlaylist.get().setGenre(updatePlaylistData.getGenre());
      }

      if (updatePlaylistData.isPrivate() != existingPlaylist.get().isPrivate()) {
         existingPlaylist.get().setPrivate(!existingPlaylist.get().isPrivate());
      }

      playlistDao.save(existingPlaylist.get());
   }
}
