package app.controller;

import app.model.User;
import app.request.RegistrationModel;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

   private static final int ID = 1;
   private static final int INVALID_ID = 0;
   private static final String USERNAME = "test";
   private static final String EMAIL = "test@test.com";
   private static final String INVALID_EMAIL = "bademail.com";
   private static final String PASSWORD = "password";

   @Mock
   private UserService userService;

   @Mock
   private Validator validator;

   @InjectMocks
   private UserController controller;

   @Test
   public void testGetUserById_Successful() {
      // Arrange
      final User user = buildUser();
      when(validator.check(true, ValidationError.BAD_VALUE, "id")).thenReturn(true);
      when(userService.getUserById(anyInt())).thenReturn(user);

      // Act
      final ResponseEntity response = controller.getUserById(ID);

      // Assert
      verify(validator).check(anyBoolean(), any(ValidationError.class), anyString());
      verifyNoMoreInteractions(validator);
      verify(userService).getUserById(anyInt());
      verifyNoMoreInteractions(userService);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertEquals(user, response.getBody());
   }

   @Test
   public void testGetUserById_InvalidId() {
      // Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, "id")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final ResponseEntity response = controller.getUserById(INVALID_ID);

      // Assert
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
      when(validator.check(true, ValidationError.BAD_VALUE, "email")).thenReturn(true);

      // Act
      final ResponseEntity response = controller.createNewUser(buildRegistrationModel());

      // Assert
      verify(validator).check(true, ValidationError.BAD_VALUE, "email");
      verifyNoMoreInteractions(validator);
      verify(userService).createNewUser(any(User.class));
      verifyNoMoreInteractions(userService);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testCreateNewUser_InvalidEmail() {
      // Arrange
      when(validator.check(false, ValidationError.BAD_VALUE, "email")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(HttpStatus.BAD_REQUEST));

      // Act
      final RegistrationModel registrationModel = buildRegistrationModel();
      registrationModel.setEmail(INVALID_EMAIL);
      final ResponseEntity response = controller.createNewUser(registrationModel);

      // Assert
      verify(validator).check(false, ValidationError.BAD_VALUE, "email");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(userService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   private RegistrationModel buildRegistrationModel() {
      final RegistrationModel model = new RegistrationModel();
      model.setUsername(USERNAME);
      model.setEmail(EMAIL);
      model.setPassword(PASSWORD);

      return model;
   }

   private ResponseEntity buildResponseEntity(final HttpStatus status) {
      return ResponseEntity.status(status).build();
   }

   private User buildUser() {
      return User.builder()
            .id(ID)
            .username(USERNAME)
            .email(EMAIL)
            .passwordHash(PASSWORD)
            .build();
   }
}
