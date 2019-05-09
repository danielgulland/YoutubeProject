package app.service;

import app.dao.UserDao;
import app.exception.ApiException;
import app.model.User;
import app.request.UpdateUserModel;
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
    * Hi.
    * @param id id
    * @throws ApiException if no User exists
    */
   //Do all the checking in UserController
   public void updateUserById(final int id, final UpdateUserModel updateUserModel) throws ApiException {
      final Optional<User> user = userDao.findById(id);
      
      if (user.isPresent()) {

         if (updateUserModel.getEmail().equals(user.get().getEmail())
               && updateUserModel.getPassword().equals(user.get().getPasswordHash())
               || updateUserModel.getEmail().equals(user.get().getEmail())
               || updateUserModel.getPassword().equals(user.get().getPasswordHash())) {
            throw new ApiException("Duplicate user", ValidationError.DUPLICATE_VALUE);
         }

         if (!updateUserModel.getPassword().equals(user.get().getPasswordHash())) {
            user.get().setPasswordHash(updateUserModel.getPassword());
         }

         if (!updateUserModel.getEmail().equals(user.get().getEmail())) {
            user.get().setEmail(updateUserModel.getEmail());
         }
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
