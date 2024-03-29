package app.controller;

import app.BaseTest;
import app.model.Playlist;
import app.model.User;
import app.request.RegistrationData;
import app.request.UpdateUserData;
import app.service.UserService;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest extends BaseTest {

   @Mock
   private UserService userService;

   @Mock
   private Validator validator;

   @InjectMocks
   private UserController controller;

   @Test
   public void testGetPlaylistsByUserId_ValidId() {
      // Arrange
      final Playlist playlist = buildPlaylist();
      when(validator.check(true, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(true);
      when(userService.getPlaylistsByUserId(VALID_ID)).thenReturn(ImmutableList.of(playlist));

      // Act
      final ResponseEntity response = controller.getPlaylistsByUserId(VALID_ID);

      // Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, ID_FIELD);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNotNull(response.getBody());
   }

   @Test
   public void testGetPlaylistsByUserId_InvalidId() {
      // Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, ID_FIELD)).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));
      
      // Act
      final ResponseEntity response = controller.getPlaylistsByUserId(INVALID_ID);

      // Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, ID_FIELD);
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testGetUserById_Successful() {
      // Arrange
      final User user = buildUser();
      when(validator.check(true, ValidationError.BAD_VALUE, "id")).thenReturn(true);
      when(userService.getUserById(anyInt())).thenReturn(user);

      // Act
      final ResponseEntity response = controller.getUserById(VALID_ID);

      // Assert
      verify(validator).check(anyBoolean(), any(ValidationError.class), anyString());
      verifyNoMoreInteractions(validator);
      verify(userService).getUserById(anyInt());
      verifyNoMoreInteractions(userService);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertEquals(user, response.getBody());
   }

   @Test
   public void testGetUserById_Invalid() {
      //Arrange
      final User user = buildUser();
      when(validator.check(false, ValidationError.BAD_VALUE, "id")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      //Act
      final ResponseEntity response = controller.getUserById(INVALID_ID);

      //Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, "id");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(userService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testCreateNewUser_Successful() {
      // Arrange
      when(validator.chain(true, ValidationError.MISSING_FIELD, "email")).thenReturn(validator);
      when(validator.chain(true, ValidationError.MISSING_FIELD, "username")).thenReturn(validator);
      when(validator.check(true, ValidationError.MISSING_FIELD, "password")).thenReturn(true);
      when(validator.check(true, ValidationError.BAD_VALUE, "email")).thenReturn(true);

      // Act
      final ResponseEntity response = controller.createNewUser(buildRegistrationData());

      // Assert
      verify(validator).chain(true, ValidationError.MISSING_FIELD, "email");
      verify(validator).chain(true, ValidationError.MISSING_FIELD, "username");
      verify(validator).check(true, ValidationError.MISSING_FIELD, "password");
      verify(validator).check(true, ValidationError.BAD_VALUE, "email");
      verifyNoMoreInteractions(validator);
      verify(userService).createNewUser(any(User.class));
      verifyNoMoreInteractions(userService);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testCreateNewUser_InvalidRegistrationData() {
      // Arrange
      when(validator.chain(false, ValidationError.MISSING_FIELD, "email")).thenReturn(validator);
      when(validator.chain(false, ValidationError.MISSING_FIELD, "username")).thenReturn(validator);
      when(validator.check(false, ValidationError.MISSING_FIELD, "password")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = controller.createNewUser(new RegistrationData());

      // Assert
      verify(validator).chain(false, ValidationError.MISSING_FIELD, "email");
      verify(validator).chain(false, ValidationError.MISSING_FIELD, "username");
      verify(validator).check(false, ValidationError.MISSING_FIELD, "password");
      verify(validator, times(0)).check(false, ValidationError.BAD_VALUE, "email");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(userService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testCreateNewUser_InvalidEmail() {
      // Arrange
      when(validator.chain(true, ValidationError.MISSING_FIELD, "email")).thenReturn(validator);
      when(validator.chain(true, ValidationError.MISSING_FIELD, "username")).thenReturn(validator);
      when(validator.check(true, ValidationError.MISSING_FIELD, "password")).thenReturn(true);
      when(validator.check(false, ValidationError.BAD_VALUE, "email")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final RegistrationData registrationData = buildRegistrationData();
      registrationData.setEmail(INVALID_EMAIL);
      final ResponseEntity response = controller.createNewUser(registrationData);

      // Assert
      verify(validator).chain(true, ValidationError.MISSING_FIELD, "email");
      verify(validator).chain(true, ValidationError.MISSING_FIELD, "username");
      verify(validator).check(true, ValidationError.MISSING_FIELD, "password");
      verify(validator).check(false, ValidationError.BAD_VALUE, "email");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(userService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testUpdateUserById_invalidId() {
      //Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, "id")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      //Act
      final UpdateUserData updateUserData = buildUpdateUserModel();
      final ResponseEntity responseEntity = controller.updateUserById(INVALID_ID, updateUserData);

      //Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, "id");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(userService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }

   @Test
   public void testUpdateUserById_invalidEmail_invalidPassword() {
      //Arrange
      when(validator.check(true, ValidationError.BAD_VALUE, "id")).thenReturn(true);
      when(validator.chain(false, ValidationError.BAD_VALUE, "email")).thenReturn(validator);
      when(validator.check(false, ValidationError.MISSING_FIELD, "oldPassword")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity((HttpStatus.BAD_REQUEST)));

      //Assert
      final UpdateUserData updateUserData = buildUpdateUserModel();
      updateUserData.setEmail(INVALID_EMAIL);
      updateUserData.setOldPassword(" ");
      final ResponseEntity responseEntity = controller.updateUserById(VALID_ID, updateUserData);

      //Act
      verify(validator).check(true, ValidationError.BAD_VALUE, "id");
      verify(validator).chain(false, ValidationError.BAD_VALUE, "email");
      verify(validator).check(false, ValidationError.MISSING_FIELD, "oldPassword");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(userService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }

   @Test
   public void testUpdateUserById_ValidEmail_And_BlankPassword() {
      //Arrange
      when(validator.check(true, ValidationError.BAD_VALUE, "id")).thenReturn(true);
      when(validator.chain(true, ValidationError.BAD_VALUE, "email")).thenReturn(validator);
      when(validator.check(true, ValidationError.MISSING_FIELD, "oldPassword")).thenReturn(true);

      //Act
      final UpdateUserData updateUserData = buildUpdateUserModel();
      updateUserData.setPassword(" ");
      final ResponseEntity response = controller.updateUserById(VALID_ID, updateUserData);

      //Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, "id");
      verify(validator).chain(true, ValidationError.BAD_VALUE, "email");
      verify(validator).check(true, ValidationError.MISSING_FIELD, "oldPassword");
      verify(userService).updateUserById(VALID_ID, updateUserData);
      verifyNoMoreInteractions(validator);
      verifyNoMoreInteractions(userService);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testUpdateUserById_ValidPassword_And_BlankEmail() {
      //Arrange
      when(validator.check(true, ValidationError.BAD_VALUE, "id")).thenReturn(true);
      when(validator.chain(true, ValidationError.BAD_VALUE, "email")).thenReturn(validator);
      when(validator.check(true, ValidationError.MISSING_FIELD, "oldPassword")).thenReturn(true);

      //Act
      final UpdateUserData updateUserData = buildUpdateUserModel();
      updateUserData.setEmail(" ");
      final ResponseEntity responseEntity = controller.updateUserById(VALID_ID, updateUserData);

      //Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, "id");
      verify(validator).chain(true, ValidationError.BAD_VALUE, "email");
      verify(validator).check(true, ValidationError.MISSING_FIELD, "oldPassword");
      verify(userService).updateUserById(VALID_ID, updateUserData);
      verifyNoMoreInteractions((validator));
      verifyNoMoreInteractions(userService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }

   @Test
   public void testGetUsers_getAll() {
      //Arrange
      final User users = buildUser();
      when(userService.getAllUsers()).thenReturn(ImmutableList.of(users));

      //Act
      final ResponseEntity responseEntity = controller.getUsers(INVALID_USERNAME);

      //Assert
      verify(userService).getAllUsers();
      verifyNoMoreInteractions(userService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNotNull(responseEntity.getBody());

   }

   @Test
   public void testGetUsers_getUsersWithFilter() {
      //Arrange
      final User users = buildUser();
      when(userService.getUsersWithFilter(USERNAME)).thenReturn(ImmutableList.of(users));

      //Act
      final ResponseEntity responseEntity = controller.getUsers((USERNAME));

      //Assert
      verify(userService).getUsersWithFilter(USERNAME);
      verifyNoMoreInteractions(userService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNotNull(responseEntity.getBody());
   }

   @Test
   public void testDeleteUserById_ValidId() {
      //Arrange
      when(validator.check(true, ValidationError.BAD_VALUE, "id")).thenReturn(true);

      //Act
      final ResponseEntity responseEntity = controller.deleteUserById(VALID_ID);

      //Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, "id");
      verify(userService).deleteUserById(VALID_ID);
      verifyNoMoreInteractions(validator);
      verifyNoMoreInteractions(userService);

      Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }

   @Test
   public void testDeleteUserById_InvalidId() {
      //Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, "id")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      //Act
      final ResponseEntity responseEntity = controller.deleteUserById(INVALID_ID);

      //Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, "id");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(userService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
      Assert.assertNull(responseEntity.getBody());
   }


}