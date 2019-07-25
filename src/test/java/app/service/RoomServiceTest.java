package app.service;

import app.BaseTest;
import app.dao.PlaylistDao;
import app.dao.RoomDao;
import app.exception.ApiException;
import app.model.Playlist;
import app.model.Room;
import app.request.UpdateRoomData;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RoomServiceTest extends BaseTest {

   @Mock
   RoomDao roomDao;

   @Mock
   PlaylistDao playlistDao;

   @InjectMocks
   RoomService roomService;

   @Test
   public void testCreateNewRoom() {
      // Arrange
      final Room room = buildRoom();

      // Act
      roomService.createNewRoom(room);

      // Assert
      verify(roomDao).save(room);
   }

   @Test
   public void testGetRoomById_ReturnsRoom() {
      // Arrange
      final Room existingRoom = buildRoom();
      when(roomDao.findById(VALID_ID)).thenReturn(Optional.of(existingRoom));

      // Act
      final Room room = roomService.getRoomById(VALID_ID);

      // Assert
      verify(roomDao).findById(VALID_ID);

      Assert.assertEquals(existingRoom, room);
   }

   @Test
   public void testGetRoomById_RoomNotFound() {
      // Arrange
      when(roomDao.findById(INVALID_ID)).thenReturn(Optional.empty());

      // Act
      try {
         roomService.getRoomById(INVALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(roomDao).findById(INVALID_ID);

         Assert.assertEquals("Room does not exist", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().size() == 1);
         Assert.assertTrue(ex.getFields().contains(ROOM));
      }
   }

   @Test
   public void testDeleteRoomById_ValidId() {
      // Arrange
      final Room existingRoom = buildRoom();
      when(roomDao.findById(VALID_ID)).thenReturn(Optional.of(existingRoom));

      // Act
      roomService.deleteRoomById(VALID_ID);

      // Assert
      verify(roomDao).findById(VALID_ID);
      verify(roomDao).deleteById(VALID_ID);
   }

   @Test
   public void testDeleteRoomById_InvalidId() {
      // Arrange
      when(roomDao.findById(INVALID_ID)).thenReturn(Optional.empty());

      // Act
      try {
         roomService.deleteRoomById(INVALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(roomDao).findById(INVALID_ID);
         verifyNoMoreInteractions(roomDao);

         Assert.assertEquals("Room does not exist", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().size() == 1);
         Assert.assertTrue(ex.getFields().contains(ROOM));
      }
   }

   @Test
   public void testGetAllRooms() {
      //Arrange
      final Room room = buildRoom();
      when(roomDao.findAll()).thenReturn(ImmutableList.of(room));

      //Act
      final List<Room> rooms = roomService.getAllRooms();

      //Assert
      verify(roomDao).findAll();
      verifyNoMoreInteractions(roomDao);

      Assert.assertEquals(1, rooms.size());
      Assert.assertEquals(room, rooms.get(0));
   }

   @Test
   public void testGetRoomsWithFilter() {
      //Arrange
      final Room room = buildRoom();
      when(roomDao.findByNameStartingWith(USERNAME)).thenReturn(ImmutableList.of(room));

      //Act
      final List<Room> rooms = roomService.getRoomsWithFilter(USERNAME);

      //Assert
      verify(roomDao).findByNameStartingWith(USERNAME);
      verifyNoMoreInteractions(roomDao);

      Assert.assertEquals(1, rooms.size());
      Assert.assertEquals(room, rooms.get(0));
   }

   @Test
   public void testUpdateRoomById_InvalidRoom() {
      //Arrange
      when(roomDao.findById(INVALID_ID)).thenReturn(Optional.empty());

      try {
         //Act
         final UpdateRoomData updateRoomData = buildUpdateRoomData();
         roomService.updateRoomById(INVALID_ID, updateRoomData);
         fail("exception not thrown");
      }
      catch (ApiException ex) {
         //Assert
         verify(roomDao).findById(INVALID_ID);
         verifyNoMoreInteractions(roomDao);
         verifyZeroInteractions(playlistDao);

         Assert.assertEquals("Room does not exist", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertEquals(ROOM, ex.getFields().get(0));
      }
   }

   @Test
   public void testUpdateRoomById_ValidPlaylistId() {
      //Arrange
      final Room room = buildRoom();
      final Playlist playlist = buildPlaylist();
      when(roomDao.findById(VALID_ID)).thenReturn(Optional.of(room));
      when(playlistDao.findById(VALID_ID)).thenReturn(Optional.of(playlist));

      //Act
      final UpdateRoomData updateRoomData = buildUpdateRoomData();
      updateRoomData.setPlaylistId(VALID_ID);
      updateRoomData.setIsPrivate(null);
      updateRoomData.setName(" ");
      roomService.updateRoomById(VALID_ID, updateRoomData);

      //Assert
      verify(roomDao).findById(VALID_ID);
      verify(playlistDao).findById(VALID_ID);
      verify(roomDao).save(any(Room.class));
      verifyNoMoreInteractions(roomDao);
      verifyNoMoreInteractions(playlistDao);

      Assert.assertEquals(updateRoomData.getPlaylistId(), room.getPlaylistId());
      Assert.assertNull(updateRoomData.getIsPrivate());
      Assert.assertEquals(" ", updateRoomData.getName());
      Assert.assertNotNull(updateRoomData.getPlaylistId());
   }

   @Test
   public void testUpdateRoomById_InvalidPlaylistId() {
      //Arrange
      final Room room = buildRoom();
      final UpdateRoomData updateRoomData = buildUpdateRoomData();
      when(roomDao.findById(VALID_ID)).thenReturn(Optional.of(room));
      when(playlistDao.findById(INVALID_ID)).thenReturn(Optional.empty());

      try {
         //Act

         updateRoomData.setPlaylistId(INVALID_ID);
         roomService.updateRoomById(VALID_ID, updateRoomData);
         fail("exception not thrown");
      }
      catch (ApiException ex) {
         //Assert
         verify(roomDao).findById(VALID_ID);
         verify(playlistDao).findById(INVALID_ID);
         verifyNoMoreInteractions(roomDao);
         verifyNoMoreInteractions(playlistDao);

         Assert.assertEquals("Playlist not found", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertEquals(PLAYLIST_ID, ex.getFields().get(0));
         Assert.assertNotNull(updateRoomData.getPlaylistId());
      }
   }

   @Test
   public void testUpdateRoomById_UpdateName() {
      //Arrange
      final Room room = buildRoom();
      when(roomDao.findById(VALID_ID)).thenReturn(Optional.of(room));

      //Act
      final UpdateRoomData updateRoomData = buildUpdateRoomData();
      updateRoomData.setPlaylistId(null);
      updateRoomData.setIsPrivate(null);
      roomService.updateRoomById(VALID_ID, updateRoomData);

      //Assert
      verify(roomDao).findById(VALID_ID);
      verify(roomDao).save(any(Room.class));
      verifyNoMoreInteractions(roomDao);
      verifyZeroInteractions(playlistDao);

      Assert.assertEquals(updateRoomData.getName(), room.getName());
      Assert.assertNull(updateRoomData.getPlaylistId());
      Assert.assertNull(updateRoomData.getIsPrivate());
   }

   @Test
   public void testUpdateRoomById_UpdatePrivate() {
      //Arrange
      final Room room = buildRoom();
      when(roomDao.findById(VALID_ID)).thenReturn(Optional.of(room));

      //Act
      final UpdateRoomData updateRoomData = buildUpdateRoomData();
      updateRoomData.setPlaylistId(null);
      updateRoomData.setName(" ");
      roomService.updateRoomById(VALID_ID, updateRoomData);

      //Assert
      verify(roomDao).findById(VALID_ID);
      verify(roomDao).save(any(Room.class));
      verifyNoMoreInteractions(roomDao);
      verifyZeroInteractions(playlistDao);

      Assert.assertEquals(updateRoomData.getIsPrivate(), room.isPrivate());
      Assert.assertEquals(" ", updateRoomData.getName());
      Assert.assertNull(updateRoomData.getPlaylistId());
   }
}