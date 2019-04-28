package app.service;

import app.dao.UserDao;
import app.exception.ApiException;
import app.model.User;
import app.validation.ValidationError;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

   private static final int USER_ID = 1;
   private static final String USERNAME = "testUser";
   private static final String EMAIL = "test@email.com";
   private static final String PASSWORD = "password";

   @Mock
   private UserDao userDao;

   @InjectMocks
   private UserService userService;

   @Test
   public void testGetUserById_ReturnsUser() {
      // Arrange
      when(userDao.findById(anyInt())).thenReturn(Optional.of(buildUser()));

      // Act
      final User response = userService.getUserById(USER_ID);

      // Assert
      verify(userDao).findById(anyInt());
      verifyNoMoreInteractions(userDao);

      Assert.assertEquals(USER_ID, response.getId().intValue());
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
         userService.getUserById(USER_ID);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(userDao).findById(anyInt());
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals(ValidationError.NOT_FOUND, ex.getError());
         Assert.assertEquals("user", ex.getField());
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
      when(userDao.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());

      // Act
      userService.createNewUser(buildUser());

      // Assert
      verify(userDao).findByUsernameOrEmail(anyString(), anyString());
      verify(userDao).save(any(User.class));
   }

   @Test
   public void testCreateNewUser_UserAlreadyExists_DuplicateUsername() {
      // Arrange
      final User user = buildUser();
      when(userDao.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));

      try {
         // Act
         userService.createNewUser(user);
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(userDao).findByUsernameOrEmail(anyString(), anyString());
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals(ValidationError.DUPLICATE_USERNAME, ex.getError());
         Assert.assertNull(ex.getField());
         Assert.assertEquals("User already exists", ex.getMessage());
      }
   }

   @Test
   public void testCreateNewUser_UserAlreadyExists_DuplicateEmail() {
      // Arrange
      when(userDao.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(buildUser()));

      try {
         // Act
         userService.createNewUser(new User(null, USERNAME + "1", EMAIL, PASSWORD));
         fail("Exception not thrown");
      } catch (ApiException ex) {
         // Assert
         verify(userDao).findByUsernameOrEmail(anyString(), anyString());
         verifyNoMoreInteractions(userDao);

         Assert.assertEquals(ValidationError.DUPLICATE_EMAIL, ex.getError());
         Assert.assertNull(ex.getField());
         Assert.assertEquals("User already exists", ex.getMessage());
      }
   }

   private User buildUser() {
      return User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .email(EMAIL)
            .passwordHash(PASSWORD)
            .build();
   }
}
