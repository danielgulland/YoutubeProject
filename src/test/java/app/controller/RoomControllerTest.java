package app.controller;

import app.BaseTest;
import app.model.Room;
import app.request.CreateRoomData;
import app.request.UpdateRoomData;
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

import com.google.common.collect.ImmutableList;

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

   @Test
   public void testGetRoomById_ValidId() {
      // Arrange
      final Room room = buildRoom();
      when(validator.check(true, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(true);
      when(roomService.getRoomById(VALID_ID)).thenReturn(room);

      // Act
      final ResponseEntity response = roomController.getRoomById(VALID_ID);

      // Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, ID_FIELD);
      verifyNoMoreInteractions(validator);
      verify(roomService).getRoomById(VALID_ID);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertEquals(room, response.getBody());
   }

   @Test
   public void testGetRoomById_InvalidId() {
      // Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = roomController.getRoomById(INVALID_ID);

      // Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(roomService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testDeleteRoomById_ValidId() {
      // Arrange
      when(validator.check(true, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(true);

      // Act
      final ResponseEntity response = roomController.deleteRoomById(VALID_ID);

      // Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, ID_FIELD);
      verifyNoMoreInteractions(validator);
      verify(roomService).deleteRoomById(VALID_ID);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testDeleteRoomById_InvalidId() {
      // Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = roomController.deleteRoomById(INVALID_ID);

      // Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).getResponseEntity();
      verifyZeroInteractions(roomService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testGetRooms_GetRoomsWithFilter() {
      //Arrange
      final Room rooms = buildRoom();
      when(roomService.getRoomsWithFilter(USERNAME)).thenReturn(ImmutableList.of(rooms));

      //Act
      final ResponseEntity responseEntity = roomController.getRooms(USERNAME);

      //Assert
      verify(roomService).getRoomsWithFilter(USERNAME);
      verifyNoMoreInteractions(roomService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNotNull(responseEntity.getBody());
   }

   @Test
   public void testGetRooms_GetAllRooms() {
      //Arrange
      final Room rooms = buildRoom();
      when(roomService.getAllRooms()).thenReturn(ImmutableList.of(rooms));

      //Act
      final ResponseEntity responseEntity = roomController.getRooms(INVALID_USERNAME);

      //Assert
      verify(roomService).getAllRooms();
      verifyNoMoreInteractions(roomService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNotNull(responseEntity.getBody());
   }

   @Test
   public void testUpdateRoomById_successful() {
      //Arrange
      when(validator.chain(true, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(validator);
      when(validator.check(true, ValidationError.BAD_VALUE, PLAYLIST_ID)).thenReturn(true);

      //Act
      final UpdateRoomData updateRoomData = buildUpdateRoomData();
      final ResponseEntity responseEntity = roomController.updateRoomById(VALID_ID, updateRoomData);

      //Assert
      verify(validator).chain(true, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).check(true, ValidationError.BAD_VALUE, PLAYLIST_ID);
      verify(roomService).updateRoomById(VALID_ID, updateRoomData);
      verifyNoMoreInteractions(validator);
      verifyNoMoreInteractions(roomService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }

   @Test
   public void testUpdateRoomById_unsuccessful() {
      //Arrange
      when(validator.chain(false, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(validator);
      when(validator.check(false, ValidationError.BAD_VALUE, PLAYLIST_ID)).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      //Act
      final UpdateRoomData updateRoomData = buildUpdateRoomData();
      updateRoomData.setPlaylistId(0);
      final ResponseEntity responseEntity = roomController.updateRoomById(INVALID_ID, updateRoomData);

      //Assert
      verify(validator).chain(false, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).check(false, ValidationError.BAD_VALUE, PLAYLIST_ID);
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(roomService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }
}
