package app.controller;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SongControllerTest {

   private static final String TITLE = "title";
   private static final String REFERENCE = "reference";

   @Mock
   private SongService songService;

   @Mock
   private Validator validator;

   @InjectMocks
   private SongController controller;

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

   private CreateSongData buildCreateSongModel() {
      final CreateSongData data = new CreateSongData();
      data.setTitle(TITLE);
      data.setReference(REFERENCE);

      return data;
   }

   private ResponseEntity buildResponseEntity(final HttpStatus status) {
      return ResponseEntity.status(status).build();
   }
}
