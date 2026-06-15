package com.karthick.expenz.users.repository;

import com.karthick.expenz.users.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
