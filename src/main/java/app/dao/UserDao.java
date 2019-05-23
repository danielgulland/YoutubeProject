package app.dao;

import app.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {
   Optional<User> findByUsername(String username);

   @Query("SELECT email FROM User WHERE email = :email")
   Optional<User> findByEmail(@Param("email") String email);

   List<User> findByUsernameOrEmail(String username, String email);

   @Query("SELECT email FROM User WHERE username = :username")
   String getEmailFromUsername(@Param("username") String username);
}