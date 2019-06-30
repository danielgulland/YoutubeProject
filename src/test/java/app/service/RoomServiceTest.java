package app.service;

import app.BaseTest;
import app.dao.RoomDao;
import app.model.Room;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


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
}
