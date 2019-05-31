package app.dao;

import app.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {
   Optional<User> findByUsername(String username);

   Optional<User> findByEmail(String email);

   List<User> findByUsernameOrEmail(String username, String email);

   @Query("SELECT email FROM User WHERE username = :username")
   String getEmailFromUsername(@Param("username") String username);

   @Query("SELECT username FROM User WHERE username LIKE CONCAT('%',:username,'%')")
   List<User> findByUsernameWithFilter(@Param("username")String username);

   List<User> findByUsernameContaining(String username);
}