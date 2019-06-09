package app.service;

import app.dao.PasswordResetDao;
import app.dao.UserDao;
import app.exception.ApiException;
import app.model.PasswordReset;
import app.model.User;
import app.request.PasswordResetData;
import app.util.EmailUtils;
import app.util.HtmlUtils;
import app.validation.ValidationError;
import lombok.NonNull;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static app.constant.FieldConstants.TOKEN;
import static app.constant.FieldConstants.USER;

@Service
public class PasswordResetService {

   @Autowired
   private PasswordResetDao passwordResetDao;

   @Autowired
   private UserDao userDao;

   @Autowired
   private EmailUtils emailUtils;

   // Time in minutes before password reset token expires
   private static final long EXPIRES_IN = 30;
   private static final String HTML_TEMPLATE = "passwordreset";
   private static final String EMAIL_SUBJECT = "Reset Your Password";
   private static final String RESET_LINK_FORMAT = "http://localhost:8000/api/resetpassword?userId=%d&token=%s";

   /**
    * Service call to create a forgotten password token.
    *
    * @param email belongs to the user who forgot their password
    * @throws ApiException if User does not exist with given email
    */
   public void forgotPassword(@NonNull final String email) throws ApiException {
      final Optional<User> user = userDao.findByEmail(email);

      if (!user.isPresent()) {
         throw new ApiException("User does not exist", ValidationError.NOT_FOUND, USER);
      }

      final String token = UUID.randomUUID().toString();

      final PasswordReset passwordReset = PasswordReset.builder()
            .userId(user.get().getId())
            .token(token)
            .expires(ZonedDateTime.now().plusMinutes(EXPIRES_IN))
            .build();

      try {
         emailUtils.sendEmail(email, EMAIL_SUBJECT, generateContent(user.get(), token));
      } catch (MessagingException | UnsupportedEncodingException exception) {
         throw new ApiException("Unable to send email", ValidationError.INTERNAL_SERVER_ERROR, exception);
      }

      passwordResetDao.save(passwordReset);
   }

   /**
    * Service call to verify that the reset token is valid for the given user.
    *
    * @param userId id of the user trying to reset password
    * @param token reset password token
    * @return The PasswordReset entry
    * @throws ApiException if token is invalid
    */
   public PasswordReset verifyToken(final int userId, @NonNull final String token) throws ApiException {
      final Optional<PasswordReset> passwordReset = passwordResetDao.findByUserId(userId);

      if (passwordReset.isPresent()
            && passwordReset.get().getToken().equals(token)
            && passwordReset.get().getExpires().isAfter(ZonedDateTime.now())) {
         return passwordReset.get();
      }

      throw new ApiException("Password reset token is invalid", ValidationError.BAD_VALUE, TOKEN);
   }

   /**
    * Service call to reset a User's password.
    *
    * @param passwordResetData contains information to update the User's password
    */
   public void resetPassword(@NonNull final PasswordResetData passwordResetData) {
      final PasswordReset passwordReset = verifyToken(passwordResetData.getUserId(), passwordResetData.getToken());
      final User user = passwordReset.getUser();

      user.setPasswordHash(passwordResetData.getPassword());

      userDao.save(user);
      passwordResetDao.delete(passwordReset);
   }

   private String generateContent(final User user, final String token) {
      final String resetUrl = String.format(RESET_LINK_FORMAT, user.getId(), token);

      final Map<String, Object> variables = new HashMap<>();
      variables.put("username", user.getUsername());
      variables.put("resetUrl", resetUrl);

      return HtmlUtils.generateHtmlContent(HTML_TEMPLATE, variables);
   }
}
