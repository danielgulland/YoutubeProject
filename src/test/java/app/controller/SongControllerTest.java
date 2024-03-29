package app.controller;

import app.BaseTest;
import app.model.Song;
import app.request.CreateSongData;
import app.service.SongService;
import app.validation.ValidationError;
import app.validation.Validator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.ImmutableList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SongControllerTest extends BaseTest {

   @Mock
   private SongService songService;

   @Mock
   private Validator validator;

   @InjectMocks
   private SongController controller;

   @Test
   public void testGetSongById_Success() {

      // Arrange
      final Song song = buildSong();
      when(validator.check(true, ValidationError.BAD_VALUE, "id")).thenReturn(true);
      when(songService.getSongById(VALID_ID)).thenReturn(song);

      // Act
      final ResponseEntity response = controller.getSongById(VALID_ID);

      // Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, "id");
      verify(songService).getSongById(VALID_ID);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertEquals(song, response.getBody());
   }

   @Test
   public void testGetSongById_InvalidId() {

      // Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, "id")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = controller.getSongById(INVALID_ID);

      // Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, "id");
      verifyZeroInteractions(songService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());

   }

   @Test
   public void testCreateNewSong_Successful() {

      // Arrange
      when(validator.chain(true, ValidationError.MISSING_FIELD, "title")).thenReturn(validator);
      when(validator.check(true, ValidationError.MISSING_FIELD, "reference")).thenReturn(true);

      // Act
      final ResponseEntity response = controller.createNewSong(buildCreateSongModel());

      // Assert
      verify(validator).chain(anyBoolean(), any(ValidationError.class), anyString());
      verify(validator).check(anyBoolean(), any(ValidationError.class), anyString());
      verifyNoMoreInteractions(validator);
      verify(songService).createNewSong(any(Song.class));

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testCreateNewSong_InvalidData() {

      // Arrange
      when(validator.chain(false, ValidationError.MISSING_FIELD, "title")).thenReturn(validator);
      when(validator.check(false, ValidationError.MISSING_FIELD, "reference")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = controller.createNewSong(new CreateSongData());

      // Assert
      verify(validator).chain(anyBoolean(), any(ValidationError.class), anyString());
      verify(validator).check(anyBoolean(), any(ValidationError.class), anyString());
      verify(validator).getResponseEntity();
      verifyZeroInteractions(songService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());

   }

   @Test
   public void testGetSongs_getSongsByFilter() {

      //Arrange
      final Song songs = buildSong();
      when(songService.getSongsByFilter(TITLE)).thenReturn(ImmutableList.of(songs));

      //Act
      final ResponseEntity responseEntity = controller.getSongs(TITLE);

      //Assert
      verify(songService).getSongsByFilter(TITLE);
      verifyNoMoreInteractions(songService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNotNull(responseEntity.getBody());
   }

   @Test
   public void testGetSongs_getAllSongs() {

      //Arrange
      final Song songs = buildSong();
      when(songService.getAllSongs()).thenReturn(ImmutableList.of(songs));

      //Act
      final ResponseEntity responseEntity = controller.getSongs(INVALID_TITLE);

      //Assert
      verify(songService).getAllSongs();
      verifyNoMoreInteractions(songService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNotNull(responseEntity.getBody());
   }

   @Test
   public void testDeleteSongById_ValidId() {

      // Arrange
      when(validator.check(true, ValidationError.BAD_VALUE, "id")).thenReturn(true);

      // Act
      final ResponseEntity response = controller.deleteSongById(VALID_ID);

      // Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, "id");
      verifyNoMoreInteractions(validator);
      verify(songService).deleteSongById(anyInt());

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testDeleteSongById_InvalidId() {

      // Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, "id")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = controller.deleteSongById(INVALID_ID);

      // Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, "id");
      verifyZeroInteractions(songService);
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }
}
