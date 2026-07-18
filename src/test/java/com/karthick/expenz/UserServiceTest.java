package com.karthick.expenz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.users.dto.UserCreateDTO;
import com.karthick.expenz.users.dto.UserDTO;
import com.karthick.expenz.users.dto.UserUpdateDTO;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.repository.UserRepository;
import com.karthick.expenz.users.service.UserService;
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
    user.setName("Kang");
    user.setEmail("kang@marvel.com");
    user.setPassword("encrypted password");
    user.setCurrencyCode("USD");
    return user;
  }

  private UserCreateDTO getTestUserCreateDTOData() {
    return new UserCreateDTO(
      "kang",
      "kang@marvel.com",
      "encrypted password",
      "USD"
    );
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
    when(userRepository.findByEmailIgnoreCase(mockUser.getEmail())).thenReturn(
      (Optional.of(mockUser))
    );

    User validUser = userService.findUserByEmail(mockUser.getEmail());
    Executable wrongId = () -> userService.findUserByEmail("testuser");

    assertEquals(mockUser, validUser);
    assertThrows(EntityNotFoundException.class, wrongId);
  }

  @Test
  public void testCreateNewUser() {
    User mockUser = getTestUserData();
    UserCreateDTO mockUserCreateDTO = getTestUserCreateDTOData();

    when(userRepository.save(any(User.class))).thenReturn(mockUser);
    when(passwordEncoder.encode(mockUserCreateDTO.getPassword())).thenReturn(
      "encrypted password"
    );

    UserDTO userDTO = userService.createUser(mockUserCreateDTO);

    assertEquals(mockUser.getId(), userDTO.getId());
    assertEquals(mockUser.getEmail(), userDTO.getEmail());
    assertEquals(mockUser.getName(), userDTO.getName());
    verify(userRepository, times(1)).save(any(User.class));
    /*
     * # need to clarify how to pass invalid type to primitive types
     * Executable invalidUser = () -> userService.createNewUser(mockUser);
     * assertThrows(BadRequestException.class, invalidUser);
     */
  }

  @Test
  public void testUpdateUser() {
    User mockUser = getTestUserData();
    when(userRepository.findById(mockUser.getId())).thenReturn(
      Optional.of(mockUser)
    );
    when(userRepository.save(mockUser)).thenReturn(mockUser);
    when(passwordEncoder.encode(any())).thenReturn("encrypted password");

    UserUpdateDTO updatedFields = new UserUpdateDTO(
      mockUser.getName(),
      "kangtheconqueror@marvel.com",
      "newpassword",
      "USD"
    );
    UserDTO validUser = userService.updateUser(mockUser.getId(), updatedFields);
    Executable wrongId = () -> userService.updateUser(2, updatedFields);

    assertEquals(mockUser.getId(), validUser.getId());
    assertEquals(mockUser.getEmail(), validUser.getEmail());
    assertEquals(mockUser.getName(), validUser.getName());
    assertThrows(EntityNotFoundException.class, wrongId);
    verify(userRepository, times(1)).save(mockUser);
  }

  @Test
  public void testDeleteUserById() {
    User mockUser = getTestUserData();
    when(userRepository.findById(mockUser.getId())).thenReturn(
      Optional.of(mockUser)
    );

    userService.deleteUser(mockUser.getId());
    Executable wrongId = () -> userService.deleteUser(2);

    assertThrows(EntityNotFoundException.class, wrongId);
    verify(userRepository, times(1)).delete(mockUser);
  }

  @Test
  public void testFindUserDTO() {
    User mockUser = getTestUserData();
    when(userRepository.findById(mockUser.getId())).thenReturn(
      Optional.of(mockUser)
    );

    UserDTO dto = userService.findUserDTO(mockUser.getId());
    Executable wrongId = () -> userService.findUserDTO(2);

    assertEquals(mockUser.getId(), dto.getId());
    assertEquals(mockUser.getName(), dto.getName());
    assertEquals(mockUser.getEmail(), dto.getEmail());
    assertThrows(EntityNotFoundException.class, wrongId);
  }

  @Test
  public void testFindUserDTOByEmail() {
    User mockUser = getTestUserData();
    when(userRepository.findByEmailIgnoreCase(mockUser.getEmail())).thenReturn(
      Optional.of(mockUser)
    );

    UserDTO dto = userService.findUserDTOByEmail(mockUser.getEmail());
    Executable wrongEmail = () ->
      userService.findUserDTOByEmail("unknown@example.com");

    assertEquals(mockUser.getId(), dto.getId());
    assertEquals(mockUser.getName(), dto.getName());
    assertEquals(mockUser.getEmail(), dto.getEmail());
    assertThrows(EntityNotFoundException.class, wrongEmail);
  }
}
