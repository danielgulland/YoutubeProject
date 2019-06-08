package app.service;

import app.dao.PlaylistDao;
import app.model.Playlist;

import java.time.ZonedDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PlaylistServiceTest {

   private static final int VALID_ID = 1;
   private static final int INVALID_ID = 0;
   private static final String ID = "id";
   private static final String GENRE = "genre";
   private static final String NAME = "name";

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

   private Playlist buildPlaylist() {
      return Playlist.builder()
            .name(NAME)
            .userId(VALID_ID)
            .isPrivate(false)
            .genre(GENRE)
            .dateCreated(ZonedDateTime.now())
            .build();
   }
}
