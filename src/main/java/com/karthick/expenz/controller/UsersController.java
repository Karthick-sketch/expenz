package com.karthick.expenz.controller;

import com.karthick.expenz.entity.User;
import com.karthick.expenz.service.UserService;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UsersController {

  private UserService userService;

  @GetMapping("/{user-id}")
  public ResponseEntity<User> getUserById(@PathVariable("user-id") long id) {
    return new ResponseEntity<>(userService.findUser(id), HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<User> createNewUser(@RequestBody User user) {
    return new ResponseEntity<>(
      userService.createUser(user),
      HttpStatus.CREATED
    );
  }

  @PatchMapping("/{user-id}")
  public ResponseEntity<User> updateUserById(
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
