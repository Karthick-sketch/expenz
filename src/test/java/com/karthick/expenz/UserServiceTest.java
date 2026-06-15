package com.karthick.expenz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.karthick.expenz.entity.User;
import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.repository.UserRepository;
import com.karthick.expenz.service.UserService;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private User getTestUserData() {
    User user = new User();
    user.setId(1);
    user.setUsername("Kang");
    user.setEmail("kang@marvel.com");
    user.setPassword("encrypted password");
    return user;
  }

  @Test
  public void testGetUserById() {
    User mockUser = getTestUserData();
    when(userRepository.findById(mockUser.getId())).thenReturn(
      (Optional.of(mockUser))
    );

    User validUser = userService.findUser(mockUser.getId());
    Executable wrongId = () -> userService.findUser(2);

    assertEquals(mockUser, validUser);
    assertThrows(EntityNotFoundException.class, wrongId);
  }

  @Test
  public void testGetUserByUsername() {
    User mockUser = getTestUserData();
    when(userRepository.findByUsername(mockUser.getUsername())).thenReturn(
      (Optional.of(mockUser))
    );

    User validUser = userService.findUserByUsername(mockUser.getUsername());
    Executable wrongId = () -> userService.findUserByUsername("testuser");

    assertEquals(mockUser, validUser);
    assertThrows(EntityNotFoundException.class, wrongId);
  }

  @Test
  public void testCreateNewUser() {
    User mockUser = getTestUserData();
    when(userRepository.save(mockUser)).thenReturn(mockUser);
    when(passwordEncoder.encode(mockUser.getPassword())).thenReturn(
      "encrypted password"
    );

    User user = userService.createUser(mockUser);

    assertEquals(mockUser, user);
    verify(userRepository, times(1)).save(mockUser);
    /*
     * # need to clarify how to pass invalid type to primitive types
     * Executable invalidUser = () -> userService.createNewUser(mockUser);
     * assertThrows(BadRequestException.class, invalidUser);
     */
  }

  @Test
  public void testUpdateUserByFields() {
    User mockUser = getTestUserData();
    when(userRepository.findById(mockUser.getId())).thenReturn(
      (Optional.of(mockUser))
    );
    when(userRepository.save(mockUser)).thenReturn(mockUser);

    Map<String, Object> updatedFields = Map.of(
      "email",
      "kangtheconqueror@marvel.com"
    );
    User validUser = userService.updateUser(mockUser.getId(), updatedFields);
    Executable wrongId = () -> userService.updateUser(2, updatedFields);

    Map<String, Object> invalidFieldType = Map.of("email", 616);
    Executable invalidUser = () ->
      userService.updateUser(mockUser.getId(), invalidFieldType);

    assertEquals(mockUser, validUser);
    assertThrows(EntityNotFoundException.class, wrongId);
    assertThrows(BadRequestException.class, invalidUser);
    verify(userRepository, times(1)).save(mockUser);
  }

  @Test
  public void testDeleteUserById() {
    User mockUser = getTestUserData();
    when(userRepository.existsById(mockUser.getId())).thenReturn(true);

    userService.deleteUser(mockUser.getId());
    Executable wrongId = () -> userService.deleteUser(2);

    assertThrows(EntityNotFoundException.class, wrongId);
    verify(userRepository, times(1)).deleteById(mockUser.getId());
  }
}
