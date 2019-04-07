package app.controller;

import app.model.User;
import app.request.RegistrationModel;
import app.service.UserService;
import app.validation.ValidationError;
import app.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class UserController {

   @Autowired
   private UserService userService;

   @Autowired
   private Validator validator;

   /**
    * Get a User by the user id.
    *
    * @param id user id
    * @return Response with status 200 and User in the body for successful call, otherwise validation response
    */
   @GetMapping("/{id}")
   public ResponseEntity getUserById(@PathVariable final int id) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, "id")) {
         final User user = userService.getUserById(id);

         return ResponseEntity.status(HttpStatus.OK).body(user);
      }

      return validator.getResponseEntity();
   }

   /**
    * Create a new User given the Registration information.
    *
    * @param registrationModel information required to create a new user
    * @return Response with status 200 and empty body for successful call, otherwise validation response
    */
   @PostMapping()
   public ResponseEntity createNewUser(@RequestBody final RegistrationModel registrationModel) {
      if (validator.chain(StringUtils.isNotBlank(registrationModel.getEmail()), ValidationError.MISSING_FIELD, "email")
            .chain(StringUtils.isNotBlank(registrationModel.getUsername()), ValidationError.MISSING_FIELD, "username")
            .check(StringUtils.isNotBlank(registrationModel.getPassword()), ValidationError.MISSING_FIELD, "password")
            && validator.check(registrationModel.getEmail().contains("@"), ValidationError.BAD_VALUE, "email")) {
         userService.createNewUser(buildUserFromRegistrationModel(registrationModel));

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   private User buildUserFromRegistrationModel(final RegistrationModel registrationModel) {
      return User.builder()
            .username(registrationModel.getUsername())
            .email(registrationModel.getEmail())
            .passwordHash(registrationModel.getPassword())
            .build();
   }
}
