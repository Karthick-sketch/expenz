package com.karthick.expenz.users.service;

import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.users.dto.UserCreateDTO;
import com.karthick.expenz.users.dto.UserDTO;
import com.karthick.expenz.users.dto.UserUpdateDTO;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.repository.UserRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

  public UserDTO updateUser(long id, UserUpdateDTO updatedUser) {
    User user = findUser(id);
    user.setName(updatedUser.name());
    user.setEmail(updatedUser.email());
    user.setPassword(passwordEncoder.encode(updatedUser.password()));

    try {
      return toUserDTO(userRepository.save(user));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public void deleteUser(long id) {
    userRepository.delete(findUser(id));
  }

  private User toUser(UserCreateDTO user) {
    return new User(user.getName(), user.getEmail(), user.getPassword());
  }

  private UserDTO toUserDTO(User user) {
    return new UserDTO(user.getId(), user.getName(), user.getEmail());
  }
}
