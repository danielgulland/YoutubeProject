package app.controller;

import app.model.PasswordReset;
import app.request.PasswordResetData;
import app.service.PasswordResetService;
import app.validation.ValidationError;
import app.validation.Validator;

import java.time.ZonedDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.ImmutableMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasswordResetControllerTest {

   private static final String EMAIL = "test@test.com";
   private static final String BAD_EMAIL = "bademail.com";
   private static final int USER_ID = 1;
   private static final int BAD_ID = 0;
   private static final String TOKEN = "token";
   private static final String BAD_TOKEN = "";
   private static final String PASSWORD = "password";

   @Mock
   private PasswordResetService passwordResetService;

   @Mock
   private Validator validator;

   @InjectMocks
   private PasswordResetController passwordResetController;

   @Test
   public void testForgotPassword_successful() {
      // Arrange
      when(validator.check(true, ValidationError.MISSING_FIELD, "email")).thenReturn(true);
      when(validator.check(true, ValidationError.BAD_VALUE, "email")).thenReturn(true);
      when(validator.getResponseEntity()).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

      // Act
      final ResponseEntity response = passwordResetController.forgotPassword(ImmutableMap.of("email", EMAIL));

      // Assert
      verify(validator).check(true, ValidationError.MISSING_FIELD, "email");
      verify(validator).check(true, ValidationError.BAD_VALUE, "email");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verify(passwordResetService).forgotPassword(EMAIL);
      verifyNoMoreInteractions(passwordResetService);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
   }

   @Test
   public void testForgotPassword_blankEmail() {
      // Arrange
      when(validator.check(false, ValidationError.MISSING_FIELD, "email")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

      // Act
      final ResponseEntity response = passwordResetController.forgotPassword(ImmutableMap.of("email", ""));

      // Assert
      verify(validator).check(false, ValidationError.MISSING_FIELD, "email");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(passwordResetService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
   }

   @Test
   public void testForgotPassword_invalidEmail() {
      // Arrange
      when(validator.check(true, ValidationError.MISSING_FIELD, "email")).thenReturn(true);
      when(validator.check(false, ValidationError.BAD_VALUE, "email")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

      // Act
      final ResponseEntity response = passwordResetController.forgotPassword(ImmutableMap.of("email", BAD_EMAIL));

      // Assert
      verify(validator).check(true, ValidationError.MISSING_FIELD, "email");
      verify(validator).check(false, ValidationError.BAD_VALUE, "email");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(passwordResetService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
   }

   @Test
   public void testVerifyToken_successful() {
      // Arrange
      final PasswordReset passwordReset = buildPasswordReset();
      when(validator.chain(true, ValidationError.BAD_VALUE, "id")).thenReturn(validator);
      when(validator.check(true, ValidationError.BAD_VALUE, "token")).thenReturn(true);
      when(passwordResetService.verifyToken(USER_ID, TOKEN)).thenReturn(passwordReset);

      // Act
      final ResponseEntity response = passwordResetController.verifyToken(USER_ID, TOKEN);

      // Assert
      verify(validator).chain(true, ValidationError.BAD_VALUE, "id");
      verify(validator).check(true, ValidationError.BAD_VALUE, "token");
      verifyNoMoreInteractions(validator);
      verify(passwordResetService).verifyToken(USER_ID, TOKEN);
      verifyNoMoreInteractions(passwordResetService);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertEquals(passwordReset, response.getBody());
   }

   @Test
   public void testVerifyToken_badIdAndBadToken() {
      // Arrange
      when(validator.chain(false, ValidationError.BAD_VALUE, "id")).thenReturn(validator);
      when(validator.check(false, ValidationError.BAD_VALUE, "token")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

      // Act
      final ResponseEntity response = passwordResetController.verifyToken(BAD_ID, BAD_TOKEN);

      // Assert
      verify(validator).chain(false, ValidationError.BAD_VALUE, "id");
      verify(validator).check(false, ValidationError.BAD_VALUE, "token");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(passwordResetService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
   }

   @Test
   public void testResetPassword_successful() {
      // Arrange
      final PasswordResetData passwordResetData = buildPasswordResetData();
      when(validator.chain(true, ValidationError.MISSING_FIELD, "token")).thenReturn(validator);
      when(validator.chain(true, ValidationError.BAD_VALUE, "id")).thenReturn(validator);
      when(validator.check(true, ValidationError.MISSING_FIELD, "password")).thenReturn(true);
      when(validator.getResponseEntity()).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

      // Act
      final ResponseEntity response = passwordResetController.resetPassword(passwordResetData);

      // Arrange
      verify(validator, times(2)).chain(eq(true), any(ValidationError.class), anyString());
      verify(validator).check(true, ValidationError.MISSING_FIELD, "password");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verify(passwordResetService).resetPassword(passwordResetData);
      verifyNoMoreInteractions(passwordResetService);

      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
   }

   @Test
   public void testResetPassword_badData() {
      // Arrange
      when(validator.chain(false, ValidationError.MISSING_FIELD, "token")).thenReturn(validator);
      when(validator.chain(false, ValidationError.BAD_VALUE, "id")).thenReturn(validator);
      when(validator.check(false, ValidationError.MISSING_FIELD, "password")).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

      // Act
      final ResponseEntity response = passwordResetController.resetPassword(new PasswordResetData());

      // Arrange
      verify(validator, times(2)).chain(eq(false), any(ValidationError.class), anyString());
      verify(validator).check(false, ValidationError.MISSING_FIELD, "password");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);
      verifyZeroInteractions(passwordResetService);

      Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
   }

   private PasswordReset buildPasswordReset() {
      final PasswordReset passwordReset = new PasswordReset();
      passwordReset.setUserId(USER_ID);
      passwordReset.setToken(TOKEN);
      passwordReset.setExpires(ZonedDateTime.now().plusMinutes(30));
      return passwordReset;
   }

   private PasswordResetData buildPasswordResetData() {
      final PasswordResetData passwordResetData = new PasswordResetData();
      passwordResetData.setUserId(USER_ID);
      passwordResetData.setToken(TOKEN);
      passwordResetData.setPassword(PASSWORD);
      return passwordResetData;
   }
}
