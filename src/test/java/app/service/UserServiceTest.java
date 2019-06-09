package app.service;

import app.BaseTest;
import app.dao.UserDao;
import app.exception.ApiException;
import app.model.User;
import app.request.UpdateUserData;
import app.validation.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest extends BaseTest {

   @Mock
   private UserDao userDao;

   @InjectMocks
   private UserService userService;

   @Test
   public void testGetUserById_ReturnsUser() {
      // Arrange
      when(userDao.findById(anyInt())).thenReturn(Optional.of(buildUser()));

      // Act
      final User response = userService.getUserById(VALID_ID);

      // Assert
      verify(userDao).findById(anyInt());
      verifyNoMoreInteractions(userDao);

      Assert.assertEquals(VALID_ID, response.getId().intValue());
      Assert.assertEquals(USERNAME, response.getUsername());
      Assert.assertEquals(EMAIL, response.getEmail());
      Assert.assertEquals(PASSWORD, response.getPasswordHash());
   }

   @Test
   public void testGetUserById_UserNotFound() {
      // Arrange
      when(userDao.findById(anyInt())).thenReturn(Optional.empty());

      try {
         // Act
         userService.getUserById(VALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(userDao).findById(anyInt());
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertEquals("user", ex.getFields().get(0));
         Assert.assertEquals("User does not exist", ex.getMessage());
      }
   }

   @Test
   public void testGetAllUsers() {
      // Arrange
      final User user = buildUser();
      when(userDao.findAll()).thenReturn(ImmutableList.of(user));

      // Act
      final List<User> users = userService.getAllUsers();

      // Assert
      verify(userDao).findAll();
      verifyNoMoreInteractions(userDao);

      Assert.assertEquals(1, users.size());
      Assert.assertEquals(user, users.get(0));
   }

   @Test
   public void testCreateNewUser_Success() {
      // Arrange
      when(userDao.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Collections.emptyList());

      // Act
      userService.createNewUser(buildUser());

      // Assert
      verify(userDao).findByUsernameOrEmail(anyString(), anyString());
      verify(userDao).save(any(User.class));
      verifyNoMoreInteractions(userDao);
   }


   @Test
   public void testCreateNewUser_UserAlreadyExists_DuplicateUsername() {
      // arrange
      final User user = buildUser();
      final User existingUser = buildUser();
      existingUser.setEmail(DIFFERENT_EMAIL);

      when(userDao.findByUsernameOrEmail(anyString(),anyString())).thenReturn(ImmutableList.of(existingUser));

      // act
      try {
         userService.createNewUser(user);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // assert
         verify(userDao).findByUsernameOrEmail(anyString(), anyString());
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals(ValidationError.DUPLICATE_VALUE, ex.getError());
         Assert.assertEquals("User already exists", ex.getMessage());
         Assert.assertEquals(1, ex.getFields().size());
         Assert.assertEquals("username", ex.getFields().get(0));
      }
   }

   @Test
   public void testCreateNewUser_UserAlreadyExists_DuplicateEmail() {
      // arrange
      final User user = buildUser();
      final User existingUser = buildUser();
      existingUser.setUsername(DIFFERENT_USERNAME);

      when(userDao.findByUsernameOrEmail(anyString(),anyString())).thenReturn(ImmutableList.of(existingUser));

      // act
      try {
         userService.createNewUser(user);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // assert
         verify(userDao).findByUsernameOrEmail(anyString(), anyString());
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals(ValidationError.DUPLICATE_VALUE, ex.getError());
         Assert.assertEquals("User already exists", ex.getMessage());
         Assert.assertEquals(1, ex.getFields().size());
         Assert.assertEquals("email", ex.getFields().get(0));
      }
   }

   @Test
   public void testCreateNewUser_UserAlreadyExists_DuplicateUsername_And_DuplicateEmail_Same_User() {
      // arrange
      final User user = buildUser();
      when(userDao.findByUsernameOrEmail(anyString(),anyString())).thenReturn(ImmutableList.of(user));

      // act
      try {
         userService.createNewUser(user);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // assert
         verify(userDao).findByUsernameOrEmail(anyString(), anyString());
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals(ValidationError.DUPLICATE_VALUE, ex.getError());
         Assert.assertEquals("User already exists", ex.getMessage());
         Assert.assertEquals(2, ex.getFields().size());
         Assert.assertTrue(ex.getFields().contains("username"));
         Assert.assertTrue(ex.getFields().contains("email"));
      }
   }

   @Test
   public void testCreateNewUser_UserAlreadyExists_DuplicateUsername_And_DuplicateEmail_Different_Users() {
      // arrange
      final User user = buildUser();
      final User existingUser1 = buildUser();
      final User existingUser2 = buildUser();
      existingUser1.setEmail(DIFFERENT_EMAIL);
      existingUser2.setUsername(DIFFERENT_USERNAME);

      when(userDao.findByUsernameOrEmail(anyString(), anyString()))
            .thenReturn(ImmutableList.of(existingUser1, existingUser2));

      // act
      try {
         userService.createNewUser(user);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // assert
         verify(userDao).findByUsernameOrEmail(anyString(), anyString());
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals(ValidationError.DUPLICATE_VALUE, ex.getError());
         Assert.assertEquals("User already exists", ex.getMessage());
         Assert.assertEquals(2, ex.getFields().size());
         Assert.assertTrue(ex.getFields().contains("username"));
         Assert.assertTrue(ex.getFields().contains("email"));
      }
   }

   @Test
   public void testUpdateUser_Success_Email_And_Password() {
      //Arrange
      final User user = buildUser();
      user.setPasswordHash(OLD_PASSWORD);

      when(userDao.findById(VALID_ID)).thenReturn(Optional.of(user));
      when(userDao.findByEmail(anyString())).thenReturn(Optional.empty());

      //Act
      final UpdateUserData updateUserData = buildUpdateUserData();
      userService.updateUserById(VALID_ID, updateUserData);

      //Assert
      verify(userDao).findById(VALID_ID);
      verify(userDao).findByEmail(anyString());
      verify(userDao).save(any(User.class));
      verifyNoMoreInteractions(userDao);

      Assert.assertEquals(updateUserData.getEmail(), user.getEmail());
      Assert.assertEquals(updateUserData.getPassword(), user.getPasswordHash());
      Assert.assertNotNull(updateUserData.getEmail());
      Assert.assertNotNull(updateUserData.getPassword());
   }

   @Test
   public void testUpdateUser_validEmail() {
      //Arrange
      final User user = buildUser();

      when(userDao.findById(VALID_ID)).thenReturn(Optional.of(user));
      when(userDao.findByEmail(anyString())).thenReturn((Optional.empty()));

      //Act
      final UpdateUserData updateUserData = buildUpdateUserData();
      updateUserData.setEmail(DIFFERENT_EMAIL);
      updateUserData.setPassword(null);
      updateUserData.setOldPassword(null);
      userService.updateUserById(VALID_ID, updateUserData);

      //Assert
      verify(userDao).findById(VALID_ID);
      verify(userDao).findByEmail(anyString());
      verify(userDao).save(any(User.class));
      verifyNoMoreInteractions(userDao);

      Assert.assertEquals(updateUserData.getEmail(), user.getEmail());
      Assert.assertEquals(PASSWORD, user.getPasswordHash());
   }

   @Test
   public void testUpdateUserById_validPassword() {
      //Arrange
      final User user = buildUser();

      when(userDao.findById(VALID_ID)).thenReturn(Optional.of(user));

      //Act
      final UpdateUserData updateUserData = buildUpdateUserData();
      updateUserData.setOldPassword(PASSWORD);
      updateUserData.setPassword("newPassword");
      updateUserData.setEmail(null);
      userService.updateUserById(VALID_ID, updateUserData);

      //Arrange
      verify(userDao).findById(VALID_ID);
      verify(userDao).save(any(User.class));
      verify(userDao, never()).findByEmail(anyString());
      verifyNoMoreInteractions(userDao);

      Assert.assertEquals(updateUserData.getPassword(), user.getPasswordHash());
      Assert.assertEquals(EMAIL, user.getEmail());
   }

   @Test
   public void testUpdateUserById_validPassword_invalidOldPassword() {
      //Arrange
      final User user = buildUser();

      when(userDao.findById(VALID_ID)).thenReturn(Optional.of(user));

      //Act
      final UpdateUserData updateUserData = buildUpdateUserData();
      updateUserData.setOldPassword(null);
      updateUserData.setPassword("newPassword");
      updateUserData.setEmail(null);
      userService.updateUserById(VALID_ID, updateUserData);

      //Arrange
      verify(userDao).findById(VALID_ID);
      verify(userDao).save(any(User.class));
      verify(userDao, never()).findByEmail(anyString());
      verifyNoMoreInteractions(userDao);

      Assert.assertNotEquals(updateUserData.getPassword(), user.getPasswordHash());
      Assert.assertEquals(EMAIL, user.getEmail());
   }

   @Test
   public void testUpdateUserById_InvalidId() {
      //Arrange
      when(userDao.findById(VALID_ID)).thenReturn(Optional.empty());

      //Act
      try {
         final UpdateUserData updateUserData = buildUpdateUserData();
         userService.updateUserById(VALID_ID, updateUserData);
         fail("exception not thrown");
      } catch (ApiException ex) {
         //Assert
         verify(userDao).findById(VALID_ID);
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals("User does not exist", ex.getMessage());
         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertEquals(1, ex.getFields().size());
         Assert.assertEquals("user", ex.getFields().get(0));
      }
   }

   @Test
   public void testUpdateUserByID_EmailAlreadyExists() {
      // Arrange
      final User user = buildUser();
      user.setPasswordHash(OLD_PASSWORD);
      final UpdateUserData updateUserData = buildUpdateUserData();
      when(userDao.findById(VALID_ID)).thenReturn(Optional.of(user));
      when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(user));

      // Act
      try {
         userService.updateUserById(VALID_ID, updateUserData);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(userDao).findById(VALID_ID);
         verify(userDao).findByEmail(EMAIL);
         verify(userDao, times(0)).save(user);
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals("Email already exists", ex.getMessage());
         Assert.assertEquals(ValidationError.DUPLICATE_VALUE, ex.getError());
         Assert.assertEquals(1, ex.getFields().size());
         Assert.assertTrue(ex.getFields().contains("email"));
         Assert.assertNotNull(updateUserData.getEmail());
      }
   }

   @Test
   public void testUpdateUserByID_passwordDoesNotMatchDatabase() {
      // Arrange
      final User user = buildUser();
      final UpdateUserData updateUserData = buildUpdateUserData();
      when(userDao.findById(VALID_ID)).thenReturn(Optional.of(user));

      // Act
      try {
         userService.updateUserById(VALID_ID, updateUserData);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(userDao).findById(VALID_ID);
         verify(userDao, times(0)).findByEmail(EMAIL);
         verify(userDao, times(0)).save(user);
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals("Old password isn't correct", ex.getMessage());
         Assert.assertEquals(ValidationError.BAD_VALUE, ex.getError());
         Assert.assertEquals(1, ex.getFields().size());
         Assert.assertTrue(ex.getFields().contains("oldPassword"));
         Assert.assertNotNull(updateUserData.getPassword());
      }
   }

   @Test
   public void testGetUsersWithFilter_validUsername() {
      //Arrange
      final User users = buildUser();
      when(userDao.findByUsernameStartingWith(USERNAME)).thenReturn(ImmutableList.of(users));

      //Act
      final List<User> existingUsers = userService.getUsersWithFilter(USERNAME);

      //Assert
      verify(userDao).findByUsernameStartingWith(USERNAME);
      verifyNoMoreInteractions(userDao);

      Assert.assertFalse(existingUsers.isEmpty());
   }
   
   @Test
   public void testDeleteUserById_validID() {
      //Arrange
      final User user = buildUser();
      when(userDao.findById(VALID_ID)).thenReturn(Optional.of(user));

      //Act
      userService.deleteUserById(VALID_ID);

      //Assert
      verify(userDao).findById(VALID_ID);
      verify(userDao).deleteById(VALID_ID);
      verifyNoMoreInteractions(userDao);
   }

   @Test
   public void testDeleteUserById_invalidID() {
      //Arrange
      when(userDao.findById(INVALID_ID)).thenReturn(Optional.empty());

      //Act
      try {
         userService.deleteUserById(INVALID_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         //Assert
         verify(userDao).findById(INVALID_ID);
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertEquals("User does not exist", ex.getMessage());
         Assert.assertEquals(1, ex.getFields().size());
      }
   }
}