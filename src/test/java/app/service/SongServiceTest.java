package app.service;

import app.dao.SongDao;
import app.exception.ApiException;
import app.model.Song;
import app.validation.ValidationError;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SongServiceTest {

   private static final int SONG_ID = 1;
   private static final String SONG = "song";
   private static final String TITLE = "title";
   private static final String REFERENCE = "reference";

   @Mock
   private SongDao songDao;

   @InjectMocks
   private SongService songService;

   @Test
   public void testGetSongById_ReturnsSong() {

      // Arrange
      when(songDao.findById(anyInt())).thenReturn(Optional.of(buildSong()));

      // Act
      final Song response = songService.getSongById(SONG_ID);

      // Assert
      verify(songDao).findById(SONG_ID);

      Assert.assertEquals(REFERENCE, response.getReference());
      Assert.assertEquals(TITLE, response.getTitle());
   }

   @Test
   public void testGetSongById_SongNotFound() {

      // Arrange
      when(songDao.findById(anyInt())).thenReturn(Optional.empty());

      // Act
      try {
         final Song response = songService.getSongById(SONG_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         Assert.assertEquals("Song does not exist", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().size() == 1);
         Assert.assertTrue(ex.getFields().contains(SONG));
      }
   }

   @Test
   public void testCreateNewUser_Success() {

      // Arrange
      final Song song = buildSong();
      when(songDao.findByReference(REFERENCE)).thenReturn(Optional.empty());

      // Act
      songService.createNewSong(song);

      // Assert
      verify(songDao).findByReference(anyString());
      verify(songDao).save(any(Song.class));
   }

   @Test
   public void testCreateNewUser_DuplicateUrl() {

      // Arrange
      final Song song = buildSong();
      when(songDao.findByReference(REFERENCE)).thenReturn(Optional.of(song));

      // Act
      try {
         songService.createNewSong(song);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(songDao).findByReference(anyString());
         verifyNoMoreInteractions(songDao);

         Assert.assertEquals("Song already exists", ex.getMessage());
         Assert.assertEquals(ValidationError.DUPLICATE_VALUE, ex.getError());
         Assert.assertEquals(1, ex.getFields().size());
         Assert.assertTrue(ex.getFields().contains("reference"));
      }
   }

   private Song buildSong() {
      return Song.builder()
            .reference(REFERENCE)
            .title(TITLE)
            .build();
   }

}
