package app.service;

import app.dao.SongDao;
import app.exception.ApiException;
import app.model.Song;
import app.validation.ValidationError;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static app.constant.FieldConstants.REFERENCE;

@Service
public class SongService {

   @Autowired
   private SongDao songDao;

   /**
    * Service call for creating a new song.
    * Checks if a song already exists with the given reference.
    *
    * @param song contains song information
    */
   public void createNewSong(final Song song) throws ApiException {
      final Optional<Song> existingSong = songDao.findByReference(song.getReference());

      if (existingSong.isPresent()) {
         throw new ApiException("Song already exists", ValidationError.DUPLICATE_VALUE, REFERENCE);
      }

      songDao.save(song);
   }
}
