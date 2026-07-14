package com.karthick.expenz.users.controller;

import com.karthick.expenz.auth.UserSession;
import com.karthick.expenz.users.dto.UserDTO;
import com.karthick.expenz.users.dto.UserUpdateDTO;
import com.karthick.expenz.users.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

  private UserService userService;

  private UserSession userSession;

  private Long userId() {
    return userSession.getAuthenticatedUserId();
  }

  @GetMapping
  public ResponseEntity<UserDTO> getUser() {
    return new ResponseEntity<>(
      userService.findUserDTO(userId()),
      HttpStatus.OK
    );
  }

  @PatchMapping
  public ResponseEntity<UserDTO> updateUser(
    @RequestBody UserUpdateDTO updatedUser
  ) {
    return new ResponseEntity<>(
      userService.updateUser(userId(), updatedUser),
      HttpStatus.OK
    );
  }

  @DeleteMapping
  public ResponseEntity<HttpStatus> deleteUser() {
    userService.deleteUser(userId());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
