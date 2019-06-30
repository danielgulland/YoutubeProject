package app.controller;

import app.BaseTest;
import app.model.Room;
import app.request.CreateRoomData;
import app.service.RoomService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoomControllerTest extends BaseTest {

   @Mock
   RoomService roomService;

   @Mock
   Validator validator;

   @InjectMocks
   RoomController roomController;

   @Test
   public void testCreateNewRoom_ValidId() {
      // Arrange
      when(validator.chain(true, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(validator);
      when(validator.check(true, ValidationError.MISSING_FIELD, NAME)).thenReturn(true);

      // Act
      final ResponseEntity response = roomController.createNewRoom(buildCreateRoomData());

      // Assert
      verify(validator).chain(true, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).check(true, ValidationError.MISSING_FIELD, NAME);
      verifyNoMoreInteractions(validator);
      verify(roomService).createNewRoom(any(Room.class));

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testCreateNewRoom_InvalidId() {
      // Arrange
      when(validator.chain(false, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(validator);
      when(validator.check(false, ValidationError.MISSING_FIELD, NAME)).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = roomController.createNewRoom(new CreateRoomData());

      // Assert
      verify(validator).chain(false, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).check(false, ValidationError.MISSING_FIELD, NAME);
      verify(validator).getResponseEntity();
      verifyZeroInteractions(roomService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }
}
