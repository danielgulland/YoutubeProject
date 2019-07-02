package app.service;

import app.BaseTest;
import app.dao.RoomDao;
import app.exception.ApiException;
import app.model.Room;
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
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RoomServiceTest extends BaseTest {

   @Mock
   RoomDao roomDao;

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
         verify(roomDao).findById(INVALID_ID);

         Assert.assertEquals("Room does not exist", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertTrue(ex.getFields().size() == 1);
         Assert.assertTrue(ex.getFields().contains(ROOM));
      }
   }
}
