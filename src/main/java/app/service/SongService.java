package app.service;

import app.dao.SongDao;
import app.exception.ApiException;
import app.model.Song;
import app.validation.ValidationError;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static app.constant.FieldConstants.REFERENCE;
import static app.constant.FieldConstants.SONG;

@Service
public class SongService {

   @Autowired
   private SongDao songDao;

   /**
    * Service call to get a song by id.
    *
    * @param id song id to check for
    * @return Song found for given id
    * @throws ApiException if no Song exists for given id
    */
   public Song getSongById(final int id) throws ApiException {
      final Optional<Song> song = songDao.findById(id);

      if (song.isPresent()) {
         return song.get();
      }

      throw new ApiException("Song does not exist", ValidationError.NOT_FOUND, SONG);
   }

   /**
    * Service call to get a list of all songs.
    *
    * @return List of all songs
    */
   public List<Song> getAllSongs() {
      return songDao.findAll();
   }

   /**
    * Service call to get a list of songs based on the title.
    *
    * @param title title used to search for songs
    * @return List of songs that match the title
    */
   public List<Song> getSongsByFilter(final String title) {
      return songDao.findByTitleContaining(title);
   }

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
