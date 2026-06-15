package com.karthick.expenz.users.controller;

import com.karthick.expenz.users.dto.UserCreateDTO;
import com.karthick.expenz.users.dto.UserDTO;
import com.karthick.expenz.users.service.UserService;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UsersController {

  private UserService userService;

  @GetMapping("/{user-id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable("user-id") long id) {
    return new ResponseEntity<>(userService.findUserDTO(id), HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<UserDTO> createNewUser(
    @RequestBody UserCreateDTO user
  ) {
    return new ResponseEntity<>(
      userService.createUser(user),
      HttpStatus.CREATED
    );
  }

  @PatchMapping("/{user-id}")
  public ResponseEntity<UserDTO> updateUserById(
    @PathVariable("user-id") long id,
    @RequestBody Map<String, Object> updatedUser
  ) {
    return new ResponseEntity<>(
      userService.updateUser(id, updatedUser),
      HttpStatus.OK
    );
  }

  @DeleteMapping("/{user-id}")
  public ResponseEntity<HttpStatus> deleteUserById(
    @PathVariable("user-id") long id
  ) {
    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
