package app.service;

import app.BaseTest;
import app.dao.PasswordResetDao;
import app.dao.UserDao;
import app.exception.ApiException;
import app.model.PasswordReset;
import app.model.User;
import app.util.EmailUtils;
import app.validation.ValidationError;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.mail.MessagingException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasswordResetServiceTest extends BaseTest {

   @Mock
   private PasswordResetDao passwordResetDao;

   @Mock
   private UserDao userDao;

   @Mock
   private EmailUtils emailUtils;

   @InjectMocks
   private PasswordResetService passwordResetService;

   @Captor
   private ArgumentCaptor<User> userArgumentCaptor;

   @Test
   public void testForgotPassword_successful() throws UnsupportedEncodingException, MessagingException {
      // Arrange
      when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(buildUser()));

      // Act
      passwordResetService.forgotPassword(EMAIL);

      // Assert
      verify(userDao).findByEmail(EMAIL);
      verifyNoMoreInteractions(userDao);
      verify(emailUtils).sendEmail(anyString(), anyString(), anyString());
      verifyNoMoreInteractions(emailUtils);
      verify(passwordResetDao).save(any(PasswordReset.class));
      verifyNoMoreInteractions(passwordResetDao);
   }

   @Test
   public void testForgotPassword_sendEmailFailed() throws UnsupportedEncodingException, MessagingException {
      // Arrange
      when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(buildUser()));
      doThrow(MessagingException.class).when(emailUtils).sendEmail(anyString(), anyString(), anyString());

      // Act
      try {
         passwordResetService.forgotPassword(EMAIL);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(userDao).findByEmail(EMAIL);
         verifyNoMoreInteractions(userDao);
         verify(emailUtils).sendEmail(anyString(), anyString(), anyString());
         verifyNoMoreInteractions(emailUtils);
         verifyZeroInteractions(passwordResetDao);

         Assert.assertEquals(ValidationError.INTERNAL_SERVER_ERROR, ex.getError());
         Assert.assertTrue(ex.getFields().isEmpty());
         Assert.assertEquals("Unable to send email", ex.getMessage());
      }
   }

   @Test
   public void testForgotPassword_userNotFound() {
      // Arrange
      when(userDao.findByEmail(EMAIL)).thenReturn(Optional.empty());

      try {
         // Act
         passwordResetService.forgotPassword(EMAIL);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(userDao).findByEmail(EMAIL);
         verifyNoMoreInteractions(userDao);
         verifyZeroInteractions(passwordResetDao);

         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertEquals("user", ex.getFields().get(0));
         Assert.assertEquals("User does not exist", ex.getMessage());
      }
   }

   @Test
   public void testVerifyToken_successful() {
      // Arrange
      final PasswordReset passwordReset = buildPasswordReset();
      when(passwordResetDao.findByUserId(VALID_ID)).thenReturn(Optional.of(passwordReset));

      // Act
      final PasswordReset response = passwordResetService.verifyToken(VALID_ID, TOKEN);

      // Assert
      verify(passwordResetDao).findByUserId(VALID_ID);
      verifyNoMoreInteractions(passwordResetDao);
      verifyZeroInteractions(userDao);

      Assert.assertEquals(passwordReset, response);
   }

   @Test
   public void testVerifyToken_tokenMismatch() {
      // Arrange
      final PasswordReset passwordReset = buildPasswordReset();
      when(passwordResetDao.findByUserId(VALID_ID)).thenReturn(Optional.of(passwordReset));

      // Act
      try {
         passwordResetService.verifyToken(VALID_ID, TOKEN + "wrong");
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(passwordResetDao).findByUserId(VALID_ID);
         verifyNoMoreInteractions(passwordResetDao);
         verifyZeroInteractions(userDao);

         Assert.assertEquals(ValidationError.BAD_VALUE, ex.getError());
         Assert.assertEquals("token", ex.getFields().get(0));
         Assert.assertEquals("Password reset token is invalid", ex.getMessage());
      }
   }

   @Test
   public void testVerifyToken_tokenExpired() {
      // Arrange
      final PasswordReset passwordReset = buildPasswordReset();
      passwordReset.setExpires(ZonedDateTime.now().minusMinutes(30));
      when(passwordResetDao.findByUserId(VALID_ID)).thenReturn(Optional.of(passwordReset));

      // Act
      try {
         passwordResetService.verifyToken(VALID_ID, TOKEN);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(passwordResetDao).findByUserId(VALID_ID);
         verifyNoMoreInteractions(passwordResetDao);
         verifyZeroInteractions(userDao);

         Assert.assertEquals(ValidationError.BAD_VALUE, ex.getError());
         Assert.assertEquals("token", ex.getFields().get(0));
         Assert.assertEquals("Password reset token is invalid", ex.getMessage());
      }
   }

   @Test
   public void testVerifyToken_noPasswordReset() {
      // Arrange
      when(passwordResetDao.findByUserId(VALID_ID)).thenReturn(Optional.empty());

      // Act
      try {
         passwordResetService.verifyToken(VALID_ID, TOKEN);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(passwordResetDao).findByUserId(VALID_ID);
         verifyNoMoreInteractions(passwordResetDao);
         verifyZeroInteractions(userDao);

         Assert.assertEquals(ValidationError.BAD_VALUE, ex.getError());
         Assert.assertEquals("token", ex.getFields().get(0));
         Assert.assertEquals("Password reset token is invalid", ex.getMessage());
      }
   }

   @Test
   public void testResetPassword_successful() {
      // Arrange
      final PasswordReset passwordReset = buildPasswordReset();
      when(passwordResetDao.findByUserId(VALID_ID)).thenReturn(Optional.of(passwordReset));

      // Act
      passwordResetService.resetPassword(buildPasswordResetData());

      // Assert
      verify(passwordResetDao).findByUserId(VALID_ID);
      verify(userDao).save(userArgumentCaptor.capture());
      verify(passwordResetDao).delete(passwordReset);
      verifyNoMoreInteractions(userDao);
      verifyNoMoreInteractions(passwordResetDao);

      Assert.assertEquals(NEW_PASSWORD, userArgumentCaptor.getValue().getPasswordHash());
   }

   @Test(expected = NullPointerException.class)
   public void testForgotPassword_emailNonNull() {
      passwordResetService.forgotPassword(null);
   }

   @Test(expected = NullPointerException.class)
   public void testVerifyToken_tokenNonNull() {
      passwordResetService.verifyToken(VALID_ID, null);
   }

   @Test(expected = NullPointerException.class)
   public void testResetPassword_passwordResetDataNonNull() {
      passwordResetService.resetPassword(null);
   }
}
