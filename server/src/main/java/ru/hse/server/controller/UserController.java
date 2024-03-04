package ru.hse.server.controller;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.hse.database.entities.User;

import org.springframework.http.ResponseEntity;
import ru.hse.server.service.UserService;

import java.util.Arrays;

@RestController
@RequestMapping("/users")
public class UserController {

    static Logger logger = LoggerFactory.getLogger(UserController.class);

    UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity postUser(/*@RequestBody User user*/ @RequestParam String name, String password) {
        try {
            var user = new User(name, password);
            userService.registration(user);
            logger.info("User {} saved", user);
            return ResponseEntity.ok("User saved"); // TODO: add logging and chng message
        } catch (Exception /*EntityExistsException*/ e) {
            //logger.error("User {} does not registered, error message: {}", user, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    @GetMapping("/userById")
    public ResponseEntity getUserById(@RequestParam Long id) {
        try {
            var user = userService.getUserByID(id);
            logger.debug("Find user={} with id={}", user, id);
            return ResponseEntity.ok().body(user);
        } catch (EntityNotFoundException e) {
            logger.error("Can not find user with id={}, error message: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/userByLogin")
    public ResponseEntity getUserByLogin(@RequestParam String login) {
        try {
            var user = userService.getUserByLogin(login);
            logger.debug("Find user={} with login={}", user, login);
            return ResponseEntity.ok().body(user);
        } catch (EntityNotFoundException e) {
            logger.error("Can not find user with login={}, error message: {}", login, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/userById")
    public ResponseEntity deleteUserById(@RequestParam Long id) {
        try {
            userService.deleteUserById(id);
            logger.info("User with id={} deleted", id);
            return ResponseEntity.ok().body("User deleted");
        } catch (EntityNotFoundException e) {
            logger.error("Can not delete user with id={}, error message: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }
}
