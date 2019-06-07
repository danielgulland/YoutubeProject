package app.controller;

import app.request.PasswordResetData;
import app.service.PasswordResetService;
import app.util.EmailUtils;
import app.validation.ValidationError;
import app.validation.Validator;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static app.constant.FieldConstants.EMAIL;
import static app.constant.FieldConstants.ID;
import static app.constant.FieldConstants.PASSWORD;
import static app.constant.FieldConstants.TOKEN;

@RestController
public class PasswordResetController {

   @Autowired
   private PasswordResetService passwordResetService;

   @Autowired
   private Validator validator;

   /**
    * Create a new password reset token for the user.
    *
    * @param data Request body containing the user's email
    * @return Response with status 200 if token is created, otherwise validator response
    */
   @PostMapping("/forgotpassword")
   public ResponseEntity forgotPassword(@RequestBody final Map<String, String> data) {
      final String email = data.get(EMAIL);

      if (validator.check(StringUtils.isNotBlank(email), ValidationError.MISSING_FIELD, EMAIL)
            && validator.check(EmailUtils.isEmailValid(email), ValidationError.BAD_VALUE, EMAIL)) {
         passwordResetService.forgotPassword(email);
      }

      return validator.getResponseEntity();
   }

   /**
    * Verify that the reset token is valid and not expired.
    *
    * @param userId id of the user that the token belongs to
    * @param token unique reset password token
    * @return Response with status 200 if verified, otherwise validator response
    */
   @GetMapping("/resetpassword")
   public ResponseEntity verifyToken(@RequestParam final int userId, @RequestParam final String token) {
      if (validator.chain(userId > 0, ValidationError.BAD_VALUE, ID)
            .check(StringUtils.isNotBlank(token), ValidationError.BAD_VALUE, TOKEN)) {
         return ResponseEntity.status(HttpStatus.OK).body(passwordResetService.verifyToken(userId, token));
      }

      return validator.getResponseEntity();
   }

   /**
    * Reset the user's password.
    *
    * @param data information required to reset a user's password
    * @return Response with status 200 if successful, otherwise validator response
    */
   @PostMapping("/resetpassword")
   public ResponseEntity resetPassword(@RequestBody final PasswordResetData data) {
      if (validator.chain(data.getUserId() > 0, ValidationError.BAD_VALUE, ID)
            .chain(StringUtils.isNotBlank(data.getToken()), ValidationError.MISSING_FIELD, TOKEN)
            .check(StringUtils.isNotBlank(data.getPassword()), ValidationError.MISSING_FIELD, PASSWORD)) {
         passwordResetService.resetPassword(data);
      }

      return validator.getResponseEntity();
   }
}
