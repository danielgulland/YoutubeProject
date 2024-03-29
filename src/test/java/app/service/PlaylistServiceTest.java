package app.service;

import app.BaseTest;
import app.dao.PlaylistDao;
import app.dao.PlaylistSongDao;
import app.dao.SongDao;
import app.exception.ApiException;
import app.model.Playlist;
import app.model.PlaylistSong;
import app.model.Song;
import app.request.UpdatePlaylistData;
import app.validation.ValidationError;

import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlaylistServiceTest extends BaseTest {

   @Mock
   private PlaylistDao playlistDao;

   @Mock
   private SongDao songDao;

   @Mock
   private PlaylistSongDao playlistSongDao;

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

   @Test
   public void testUpdatePlaylistById_InvalidId() {

      // Arrange
      when(playlistDao.findById(anyInt())).thenReturn(Optional.empty());

      // Act
      try {
         playlistService.updatePlaylistById(VALID_ID, buildUpdatePlaylistData());
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(playlistDao).findById(anyInt());

         Assert.assertEquals("Playlist does not exist", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertEquals(1, ex.getFields().size());
         Assert.assertTrue(ex.getFields().contains(PLAYLIST));
      }
   }

   @Test
   public void testUpdatePlaylistById_ValidName() {

      // Arrange
      final Playlist playlist = buildPlaylist();
      when(playlistDao.findById(anyInt())).thenReturn(Optional.of(playlist));

      // Act
      final UpdatePlaylistData updatePlaylistData = new UpdatePlaylistData();
      updatePlaylistData.setName(NEW_NAME);
      playlistService.updatePlaylistById(VALID_ID, updatePlaylistData);

      // Assert
      verify(playlistDao).findById(VALID_ID);
      verify(playlistDao).save(playlist);
      verifyNoMoreInteractions(playlistDao);

      Assert.assertEquals(NEW_NAME, playlist.getName());
      Assert.assertEquals(GENRE, playlist.getGenre());
      Assert.assertEquals(false, playlist.isPrivate());
   }

   @Test
   public void testUpdatePlaylistById_ValidGenre() {

      // Arrange
      final Playlist playlist = buildPlaylist();
      when(playlistDao.findById(anyInt())).thenReturn(Optional.of(playlist));

      // Act
      final UpdatePlaylistData updatePlaylistData = new UpdatePlaylistData();
      updatePlaylistData.setGenre(NEW_GENRE);
      playlistService.updatePlaylistById(VALID_ID, updatePlaylistData);

      // Assert
      verify(playlistDao).findById(VALID_ID);
      verify(playlistDao).save(playlist);
      verifyNoMoreInteractions(playlistDao);

      Assert.assertEquals(NAME, playlist.getName());
      Assert.assertEquals(NEW_GENRE, playlist.getGenre());
      Assert.assertEquals(false, playlist.isPrivate());
   }

   @Test
   public void testUpdatePlaylistById_ChangePrivacy_FalseToTrue() {
      // Arrange
      final Playlist playlist = buildPlaylist();
      when(playlistDao.findById(anyInt())).thenReturn(Optional.of(playlist));

      // Act
      final UpdatePlaylistData updatePlaylistData = new UpdatePlaylistData();
      updatePlaylistData.setIsPrivate(true);
      playlistService.updatePlaylistById(VALID_ID, updatePlaylistData);

      // Assert
      verify(playlistDao).findById(VALID_ID);
      verify(playlistDao).save(playlist);
      verifyNoMoreInteractions(playlistDao);

      Assert.assertEquals(NAME, playlist.getName());
      Assert.assertEquals(GENRE, playlist.getGenre());
      Assert.assertEquals(true, playlist.isPrivate());
   }

   @Test
   public void testUpdatePlaylistById_ChangePrivacy_TrueToFalse() {
      // Arrange
      final Playlist playlist = buildPlaylist();
      playlist.setPrivate(true);
      when(playlistDao.findById(anyInt())).thenReturn(Optional.of(playlist));

      // Act
      final UpdatePlaylistData updatePlaylistData = buildUpdatePlaylistData();
      playlistService.updatePlaylistById(VALID_ID, updatePlaylistData);

      // Assert
      verify(playlistDao).findById(VALID_ID);
      verify(playlistDao).save(playlist);
      verifyNoMoreInteractions(playlistDao);

      Assert.assertEquals(NAME, playlist.getName());
      Assert.assertEquals(GENRE, playlist.getGenre());
      Assert.assertEquals(false, playlist.isPrivate());
   }

   @Test
   public void testAddSongToPlaylist_Successful() {

      //Arrange
      final Playlist playlist = buildPlaylist();
      final Song song = buildSong();
      when(songDao.findById(VALID_ID)).thenReturn(Optional.of(song));
      when(playlistDao.findById(VALID_ID)).thenReturn(Optional.of(playlist));

      //Act
      playlistService.addSongToPlaylist(VALID_ID, VALID_ID);

      //Assert
      verify(songDao).findById(VALID_ID);
      verify(playlistDao).findById(VALID_ID);
      verify(playlistSongDao).save(any(PlaylistSong.class));
      verifyNoMoreInteractions(songDao);
      verifyNoMoreInteractions(playlistDao);
      verifyNoMoreInteractions(playlistSongDao);
   }

   @Test
   public void testAddSongToPlaylist_SongNotFound() {

      //Arrange
      final Playlist playlist = buildPlaylist();
      when(songDao.findById(INVALID_ID)).thenReturn(Optional.empty());
      when(playlistDao.findById(INVALID_ID)).thenReturn(Optional.of(playlist));

      try {
         playlistService.addSongToPlaylist(INVALID_ID, INVALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         verify(songDao).findById(INVALID_ID);
         verify(playlistDao).findById(INVALID_ID);
         verifyNoMoreInteractions(songDao);
         verifyNoMoreInteractions(playlistDao);
         verifyZeroInteractions(playlistSongDao);

         Assert.assertEquals("Song not found", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().contains(SONG_ID));
      }
   }

   @Test
   public void testAddSongToPlaylist_PlaylistNotFound() {

      //Arrange
      when(songDao.findById(INVALID_ID)).thenReturn(Optional.empty());
      when(playlistDao.findById(INVALID_ID)).thenReturn(Optional.empty());

      try {
         playlistService.addSongToPlaylist(INVALID_ID, INVALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         verify(songDao).findById(INVALID_ID);
         verify(playlistDao).findById(INVALID_ID);
         verifyNoMoreInteractions(songDao);
         verifyNoMoreInteractions(playlistDao);
         verifyZeroInteractions(playlistSongDao);

         Assert.assertEquals("Playlist not found", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().contains(PLAYLIST_ID));
      }
   }

   @Test
   public void testDeletePlaylist_successful() {

      //Arrange
      final Playlist playlist = buildPlaylist();
      when(playlistDao.findById(VALID_ID)).thenReturn(Optional.of(playlist));

      //Act
      playlistService.deletePlaylist(VALID_ID);

      //Assert
      verify(playlistDao).findById(VALID_ID);
      verify(playlistDao).delete(any(Playlist.class));
      verifyNoMoreInteractions(playlistDao);
   }

   @Test
   public void testDeletePlaylist_unsuccessful() {

      //Arrange
      when(playlistDao.findById(INVALID_ID)).thenReturn(Optional.empty());

      try {
         //Act
         playlistService.deletePlaylist(INVALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         //Assert
         verify(playlistDao).findById(INVALID_ID);
         verifyNoMoreInteractions(playlistDao);

         Assert.assertEquals("Playlist not found", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().contains(PLAYLIST_ID));
      }
   }

   @Test
   public void testGetPlaylistsByFilter() {

      // Arrange
      final Playlist playlist = buildPlaylist();
      when(playlistDao.findByNameLikeAndGenreLike(NAME, GENRE)).thenReturn(ImmutableList.of(playlist));

      // Act
      final List<Playlist> playlists = playlistService.getPlaylistsByFilter(NAME, GENRE);

      // Assert
      verify(playlistDao).findByNameLikeAndGenreLike(NAME, GENRE);
      verifyNoMoreInteractions(playlistDao);

      Assert.assertEquals(1, playlists.size());
      Assert.assertTrue(playlists.contains(playlist));
   }

   @Test
   public void testGetSongsInPlaylist_successful() {

      //Arrange
      final Song song = buildSong();
      final Playlist playlists = buildPlaylist();
      playlists.setSongs(ImmutableList.of(song));
      when(playlistDao.findById(VALID_ID)).thenReturn(Optional.of(playlists));

      //Act
      final List<Song> songs = playlistService.getSongsInPlaylist(VALID_ID);

      //Assert
      verify(playlistDao).findById(VALID_ID);
      verifyNoMoreInteractions(playlistDao);
      Assert.assertEquals(1, songs.size());
      Assert.assertTrue(songs.contains(song));
   }

   @Test
   public void testGetSongsInPlaylist_unsuccessful() {

      //Arrange
      when(playlistDao.findById(INVALID_ID)).thenReturn(Optional.empty());

      try {
         //Act
         final List<Song> songs = playlistService.getSongsInPlaylist(INVALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         //Assert
         verify(playlistDao).findById(INVALID_ID);
         verifyNoMoreInteractions(playlistDao);
         Assert.assertEquals("Playlist not found", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().contains(PLAYLIST));
      }
   }

   @Test
   public void testDeleteSongInPlaylist_PlaylistSongNotFound() {
      // Arrange
      when(playlistSongDao.findById(VALID_ID)).thenReturn(Optional.empty());

      // Act
      try {
         playlistService.deleteSongInPlaylist(VALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(playlistSongDao).findById(VALID_ID);
         verifyNoMoreInteractions(playlistSongDao);

         Assert.assertEquals("Song does not exist in this playlist", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().size() == 1);
         Assert.assertTrue(ex.getFields().contains(PLAYLIST_SONG));
      }
   }

   @Test
   public void testDeleteSongInPlaylist_Successful() {
      // Arrange
      when(playlistSongDao.findById(VALID_ID)).thenReturn(Optional.of(buildPlaylistSong()));

      // Act
      playlistService.deleteSongInPlaylist(VALID_ID);

      // Assert
      verify(playlistSongDao).findById(VALID_ID);
      verify(playlistSongDao).delete(any(PlaylistSong.class));
   }
}

