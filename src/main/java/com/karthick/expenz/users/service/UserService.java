package com.karthick.expenz.users.service;

import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.users.dto.UserCreateDTO;
import com.karthick.expenz.users.dto.UserDTO;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.repository.UserRepository;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  public User findUser(long id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      return user.get();
    }
    throw new EntityNotFoundException(id, User.class);
  }

  public UserDTO findUserDTO(long id) {
    return toUserDTO(findUser(id));
  }

  public User findUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmailIgnoreCase(email);
    if (user.isPresent()) {
      return user.get();
    }
    throw new EntityNotFoundException(
      "The user with the email '" + email + "' does not exist in our records"
    );
  }

  public UserDTO createUser(UserCreateDTO user) {
    try {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      User newUser = toUser(user);
      return toUserDTO(userRepository.save(newUser));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public UserDTO updateUser(long id, Map<String, Object> fields) {
    User user = findUser(id);
    try {
      fields.forEach((key, value) -> {
        Field field = ReflectionUtils.findField(User.class, key);
        if (field != null) {
          field.setAccessible(true);
          ReflectionUtils.setField(field, user, value);
        }
      });
      return toUserDTO(userRepository.save(user));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public void deleteUser(long id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return;
    }
    throw new EntityNotFoundException(id, User.class);
  }

  private User toUser(UserCreateDTO user) {
    return new User(user.getUsername(), user.getEmail(), user.getPassword());
  }

  private UserDTO toUserDTO(User user) {
    return new UserDTO(user.getId(), user.getName(), user.getEmail());
  }
}
