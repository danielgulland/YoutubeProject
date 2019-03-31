package app.dao;

import app.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserDao extends CrudRepository<User, Integer> {
   Optional<User> findByUsername(String username);

   Optional<User> findByUsernameOrEmail(String username, String email);

   @Query("SELECT email FROM User WHERE username = :username")
   String getEmailFromUsername(@Param("username") String username);
}
