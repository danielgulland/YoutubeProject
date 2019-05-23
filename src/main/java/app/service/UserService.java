package app.service;

import app.dao.UserDao;
import app.exception.ApiException;
import app.model.User;
import app.request.UpdateUserData;
import app.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

   @Autowired
   private UserDao userDao;

   /**
    * Service call to get a user by id.
    *
    * @param id user id to check for
    * @return User found for given id
    * @throws ApiException if no User exists for given id
    */
   public User getUserById(final int id) throws ApiException {
      final Optional<User> user = userDao.findById(id);


      if (user.isPresent()) {
         return user.get();
      }

      throw new ApiException("User does not exist", ValidationError.NOT_FOUND, "user");
   }

   /**
    * Service call to update user by id.
    *
    * @param id user id to check for
    * @throws ApiException if no User exists for given id, if old password isn't correct, if email already exists.
    */
   public void updateUserById(final int id, final UpdateUserData updateUserData) throws ApiException {

      final Optional<User> user = userDao.findById(id);

      if (!user.isPresent()) {
         throw new ApiException("User does not exist", ValidationError.NOT_FOUND, "user");
      }

      if (updateUserData.getPassword() != null && updateUserData.getOldPassword() != null) {
         if (!updateUserData.getOldPassword().equals(user.get().getPasswordHash())) {
            throw new ApiException("Old password isn't correct", ValidationError.BAD_VALUE, "oldPassword");
         }

         user.get().setPasswordHash(updateUserData.getPassword());
      }

      if (updateUserData.getEmail() != null) {
         final Optional<User> existingEmail = userDao.findByEmail(updateUserData.getEmail());

         if (existingEmail.isPresent()) {
            throw new ApiException("Email already exists", ValidationError.DUPLICATE_VALUE, "email");
         }

         user.get().setEmail(updateUserData.getEmail());
      }

      userDao.save(user.get());
   }

   public List<User> getAllUsers() {
      return userDao.findAll();
   }

   /**
    * Service call for creating a new user.
    * Checks if a user already exists with the given username and password.
    *
    * @param user contains User information
    * @throws ApiException if User already exists
    */
   public void createNewUser(final User user) throws ApiException {
      final List<User> existingUsers = userDao.findByUsernameOrEmail(user.getUsername(), user.getEmail());

      if (!existingUsers.isEmpty()) {
         final List<String> duplicateValueFields = new ArrayList<>();
         for (User existingUser: existingUsers) {
            if (existingUser.getUsername().equals(user.getUsername())) {
               duplicateValueFields.add("username");
            }
            if (existingUser.getEmail().equals(user.getEmail())) {
               duplicateValueFields.add("email");
            }
         }

         throw new ApiException("User already exists", ValidationError.DUPLICATE_VALUE, duplicateValueFields);
      }

      userDao.save(user);
   }
}