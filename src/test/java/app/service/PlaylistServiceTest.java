package app.service;

import app.BaseTest;
import app.dao.PlaylistDao;
import app.exception.ApiException;
import app.model.Playlist;
import app.validation.ValidationError;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlaylistServiceTest extends BaseTest {

   @Mock
   private PlaylistDao playlistDao;

   @InjectMocks
   private PlaylistService playlistService;

   @Test
   public void testCreateNewPlaylist() {

      // Arrange
      final Playlist playlist = buildPlaylist();

      // Act
      playlistService.createNewPlaylist(playlist);

      // Assert
      verify(playlistDao).save(playlist);
   }

   @Test
   public void testGetPlaylistById_validId() {

      //Arrange
      final Playlist playlist = buildPlaylist();
      when(playlistDao.findById(VALID_ID)).thenReturn(Optional.of(playlist));

      //Act
      final Playlist existingPlaylist = playlistService.getPlaylistById(VALID_ID);

      //Assert
      verify(playlistDao).findById(VALID_ID);
      verifyNoMoreInteractions(playlistDao);

      Assert.assertEquals(playlist, existingPlaylist);
   }

   @Test
   public void testGetPlayListById_invalidId() {

      //Arrange
      when(playlistDao.findById(VALID_ID)).thenReturn(Optional.empty());

      //Act
      try {
         playlistService.getPlaylistById(VALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         //Assert
         verify(playlistDao).findById(VALID_ID);
         verifyNoMoreInteractions(playlistDao);

         Assert.assertEquals("Playlist not found", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().contains("playlist"));
      }
   }
}
