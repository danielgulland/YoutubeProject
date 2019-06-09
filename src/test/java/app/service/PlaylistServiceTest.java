package app.service;

import app.BaseTest;
import app.dao.PlaylistDao;
import app.model.Playlist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

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
}
