package app.service;

import app.dao.PlaylistDao;
import app.dao.UserDao;
import app.exception.ApiException;
import app.model.Playlist;
import app.model.User;
import app.request.UpdateUserData;
import app.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static app.constant.FieldConstants.EMAIL;
import static app.constant.FieldConstants.OLD_PASSWORD;
import static app.constant.FieldConstants.USER;
import static app.constant.FieldConstants.USERNAME;

@Service
public class UserService {

   @Autowired
   private UserDao userDao;

   @Autowired
   private PlaylistDao playlistDao;

   /**
    * Service call to get all playlists by user id.
    *
    * @param id user id to check for
    * @return List of Playlists found for given user id
    */
   public List<Playlist> getPlaylistsByUserId(final int id) {
      final Optional<User> user = userDao.findById(id);

      if (!user.isPresent()) {
         throw new ApiException("User does not exist", ValidationError.NOT_FOUND, USER);
      }

      return playlistDao.findByUserId(id);
   }

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

      throw new ApiException("User does not exist", ValidationError.NOT_FOUND, USER);
   }

   /**
    * Service call to update user by id.
    *
    * @param id user id to check for
    * @param updateUserData contains information to update a user
    * @throws ApiException if no User exists for given id, if old password isn't correct, if email already exists
    */
   public void updateUserById(final int id, final UpdateUserData updateUserData) throws ApiException {

      final Optional<User> user = userDao.findById(id);

      if (!user.isPresent()) {
         throw new ApiException("User does not exist", ValidationError.NOT_FOUND, USER);
      }

      if (StringUtils.isNotBlank(updateUserData.getPassword())
            && StringUtils.isNotBlank(updateUserData.getOldPassword())) {
         if (!updateUserData.getOldPassword().equals(user.get().getPasswordHash())) {
            throw new ApiException("Old password isn't correct", ValidationError.BAD_VALUE, OLD_PASSWORD);
         }

         user.get().setPasswordHash(updateUserData.getPassword());
      }

      if (StringUtils.isNotBlank(updateUserData.getEmail())) {
         final Optional<User> existingUser = userDao.findByEmail(updateUserData.getEmail());

         if (existingUser.isPresent()) {
            throw new ApiException("Email already exists", ValidationError.DUPLICATE_VALUE, EMAIL);
         }

         user.get().setEmail(updateUserData.getEmail());
      }

      userDao.save(user.get());
   }

   public List<User> getAllUsers() {
      return userDao.findAll();
   }

   /**
    * Service call to get users by username.
    *
    * @param username Username to filter by
    * @return List of Users that match the username
    */
   public List<User> getUsersWithFilter(final String username) {
      return userDao.findByUsernameStartingWith(username);
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
            if (existingUser.getUsername().toLowerCase().equals(user.getUsername().toLowerCase())) {
               duplicateValueFields.add(USERNAME);
            }
            if (existingUser.getEmail().toLowerCase().equals(user.getEmail().toLowerCase())) {
               duplicateValueFields.add(EMAIL);
            }
         }

         throw new ApiException("User already exists", ValidationError.DUPLICATE_VALUE, duplicateValueFields);
      }

      userDao.save(user);
   }

   /**
    * Service call for deleting a user by id.
    *
    * @param id user id to check for
    */
   public void deleteUserById(final int id) {
      final Optional<User> user = userDao.findById(id);

      if (user.isPresent()) {
         userDao.deleteById(id);
      }
      else {
         throw new ApiException("User does not exist", ValidationError.NOT_FOUND, USER);
      }
   }
}