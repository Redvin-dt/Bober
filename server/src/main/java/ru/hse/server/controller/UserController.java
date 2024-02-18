package ru.hse.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hse.server.entity.UserEntity;

import org.springframework.http.ResponseEntity;
import ru.hse.server.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity postUser(@RequestBody UserEntity user) {
        try {
            userService.registration(user);
            return ResponseEntity.ok("Пользователь сохранен");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/userById")
    public ResponseEntity getUserById(@RequestParam Long id) {
        try {
            var user = userService.getUserByID(id);
            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/userByLogin")
    public ResponseEntity getUserByLogin(@RequestParam String login) {
        try {
            var user = userService.getUserByLogin(login);
            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/userById")
    public ResponseEntity deleteUserById(@RequestParam Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body("Пользователь удален");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
