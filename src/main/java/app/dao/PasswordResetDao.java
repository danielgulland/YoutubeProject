package app.dao;

import app.model.PasswordReset;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetDao extends JpaRepository<PasswordReset, Integer> {
   Optional<PasswordReset> findByUserId(int userId);
}
