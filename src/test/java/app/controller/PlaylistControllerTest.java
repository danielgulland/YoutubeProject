package app.controller;

import app.BaseTest;
import app.model.Playlist;
import app.request.CreatePlaylistData;
import app.request.UpdatePlaylistData;
import app.service.PlaylistService;
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

import static app.constant.FieldConstants.PLAYLIST_ID;
import static app.constant.FieldConstants.SONG_ID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlaylistControllerTest extends BaseTest {

   @Mock
   private PlaylistService playlistService;

   @Mock
   private Validator validator;

   @InjectMocks
   private PlaylistController playlistController;

   @Test
   public void testCreateNewPlaylist_Valid() {

      // Arrange
      when(validator.chain(true, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(validator);
      when(validator.chain(true, ValidationError.MISSING_FIELD, NAME)).thenReturn(validator);
      when(validator.check(true, ValidationError.MISSING_FIELD, GENRE)).thenReturn(true);

      // Act
      final ResponseEntity response = playlistController.createNewPlaylist(buildCreatePlaylistData());

      // Assert
      verify(validator).chain(true, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).chain(true, ValidationError.MISSING_FIELD, NAME);
      verify(validator).check(true, ValidationError.MISSING_FIELD, GENRE);
      verify(playlistService).createNewPlaylist(any(Playlist.class));

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testCreateNewPlaylist_Invalid() {

      // Arrange
      when(validator.chain(false, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(validator);
      when(validator.chain(false, ValidationError.MISSING_FIELD, NAME)).thenReturn(validator);
      when(validator.check(false, ValidationError.MISSING_FIELD, GENRE)).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = playlistController.createNewPlaylist(new CreatePlaylistData());

      // Assert
      verify(validator).chain(false, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).chain(false, ValidationError.MISSING_FIELD, NAME);
      verify(validator).check(false, ValidationError.MISSING_FIELD, GENRE);
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(playlistService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testGetPlaylistById_successful() {

      //Arrange
      final Playlist playlist = buildPlaylist();
      when(validator.check(true, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(true);
      when(playlistService.getPlaylistById(VALID_ID)).thenReturn(playlist);

      //Act
      final ResponseEntity responseEntity = playlistController.getPlaylistById(VALID_ID);

      //Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, ID_FIELD);
      verify(playlistService).getPlaylistById(VALID_ID);
      verifyNoMoreInteractions(playlistService);
      verifyNoMoreInteractions(validator);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertEquals(playlist, responseEntity.getBody());
   }

   @Test
   public void testGetPlaylistById_unsuccessful() {

      //Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      //Act
      final ResponseEntity responseEntity = playlistController.getPlaylistById(INVALID_ID);

      //Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(playlistService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }

   @Test
   public void testUpdatePlaylistById_ValidId() {

      // Arrange
      when(validator.check(true, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(true);

      // Act
      final ResponseEntity response = playlistController.updatePlaylistById(VALID_ID, buildUpdatePlaylistData());

      // Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, ID_FIELD);
      verify(playlistService).updatePlaylistById(anyInt(), any(UpdatePlaylistData.class));

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testUpdatePlaylistById_InvalidId() {

      // Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = playlistController.updatePlaylistById(INVALID_ID, buildUpdatePlaylistData());

      // Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, ID_FIELD);

   }

   @Test
   public void testAddSongToPlaylist_successful() {
      //Arrange
      when(validator.chain(true, ValidationError.BAD_VALUE, SONG_ID)).thenReturn(validator);
      when(validator.check(true, ValidationError.BAD_VALUE, PLAYLIST_ID)).thenReturn(true);

      //Act
      final ResponseEntity responseEntity = playlistController.addSongToPlaylist(VALID_ID, VALID_ID);

      //Assert
      verify(validator).chain(true, ValidationError.BAD_VALUE, SONG_ID);
      verify(validator).check(true, ValidationError.BAD_VALUE, PLAYLIST_ID);
      verify(playlistService).addSongToPlaylist(VALID_ID, VALID_ID);
      verifyNoMoreInteractions(validator);
      verifyNoMoreInteractions(playlistService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }

   @Test
   public void testAddSongToPlaylist_unsuccessful() {
      //Arrange
      when(validator.chain(false, ValidationError.BAD_VALUE, SONG_ID)).thenReturn(validator);
      when(validator.check(false, ValidationError.BAD_VALUE, PLAYLIST_ID)).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      //Act
      final ResponseEntity responseEntity = playlistController.addSongToPlaylist(INVALID_ID, INVALID_ID);

      //Assert
      verify(validator).chain(false, ValidationError.BAD_VALUE, SONG_ID);
      verify(validator).check(false, ValidationError.BAD_VALUE, PLAYLIST_ID);
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(playlistService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }

   @Test
   public void testGetPlaylists() {

      // Arrange
      final Playlist playlist = buildPlaylist();
      when(playlistService.getPlaylistsByFilter(NAME, GENRE)).thenReturn(ImmutableList.of(playlist));

      // Act
      final ResponseEntity response = playlistController.getPlaylists(NAME, GENRE);

      // Assert
      verify(playlistService).getPlaylistsByFilter(NAME, GENRE);
      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertEquals(ImmutableList.of(playlist), response.getBody());
   }
}
