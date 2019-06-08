package app.controller;

import app.model.User;
import app.request.RegistrationData;
import app.request.UpdateUserData;
import app.service.UserService;
import app.util.EmailUtils;
import app.validation.ValidationError;
import app.validation.Validator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static app.constant.FieldConstants.EMAIL;
import static app.constant.FieldConstants.ID;
import static app.constant.FieldConstants.OLD_PASSWORD;
import static app.constant.FieldConstants.PASSWORD;
import static app.constant.FieldConstants.USERNAME;

@RestController
@RequestMapping(path = "/users")
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
      if (validator.check(id > 0, ValidationError.BAD_VALUE, ID)) {
         return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
      }

      return validator.getResponseEntity();
   }

   /**
    * Get a list of users based on the username, otherwise every user when username is blank.
    *
    * @param username username or username prefix to search for
    * @return Response with status 200 and User in the body for successful call
    */
   @GetMapping()
   public ResponseEntity getUsers(@RequestParam(required = false) final String username) {
      final List<User> users;
      if (StringUtils.isNotBlank(username)) {
         users = userService.getUsersWithFilter(username);
      }
      else {
         users = userService.getAllUsers();
      }

      return ResponseEntity.status(HttpStatus.OK).body(users);
   }

   /**
    * Update an existing user given the information.
    *
    * @param id user id
    * @return Response with status 200 and null in the body for successful call, otherwise validation response
    */
   @PutMapping("/{id}")
   public ResponseEntity updateUserById(@PathVariable final int id,
                                        @RequestBody final UpdateUserData updateUserData) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, ID)
            && validator.chain(isEmailValid(updateUserData.getEmail()), ValidationError.BAD_VALUE, EMAIL)
            .check(isPasswordValid(updateUserData.getPassword(), updateUserData.getOldPassword()),
                  ValidationError.MISSING_FIELD, OLD_PASSWORD)) {
         userService.updateUserById(id, updateUserData);

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }
      return validator.getResponseEntity();
   }

   /**
    * Create a new User given the Registration information.
    *
    * @param registrationData information required to create a new user
    * @return Response with status 200 and empty body for successful call, otherwise validation response
    */
   @PostMapping()
   public ResponseEntity createNewUser(@RequestBody final RegistrationData registrationData) {
      if (validator.chain(StringUtils.isNotBlank(registrationData.getEmail()), ValidationError.MISSING_FIELD, EMAIL)
            .chain(StringUtils.isNotBlank(registrationData.getUsername()), ValidationError.MISSING_FIELD, USERNAME)
            .check(StringUtils.isNotBlank(registrationData.getPassword()), ValidationError.MISSING_FIELD, PASSWORD)
            && validator.check(isEmailValid(registrationData.getEmail()), ValidationError.BAD_VALUE, EMAIL)) {
         userService.createNewUser(buildUserFromRegistrationData(registrationData));

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   /**
    * Delete a User by the user id.
    *
    * @param id user id
    * @return Response with status 200 and null in the body for successful call, otherwise validation response
    */
   @DeleteMapping("/{id}")
   public ResponseEntity deleteUserById(@PathVariable final int id) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, ID)) {
         userService.deleteUserById(id);

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   private User buildUserFromRegistrationData(final RegistrationData registrationData) {
      return User.builder()
            .username(registrationData.getUsername())
            .email(registrationData.getEmail())
            .passwordHash(registrationData.getPassword())
            .build();
   }

   private boolean isEmailValid(final String email) {
      return StringUtils.isBlank(email)
            || EmailUtils.isEmailValid(email);
   }

   private boolean isPasswordValid(final String password, final String oldPassword) {
      return StringUtils.isBlank(password)
            || StringUtils.isNotBlank(oldPassword);
   }
}