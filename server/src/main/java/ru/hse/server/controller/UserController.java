package ru.hse.server.controller;

import ru.hse.server.proto.EntitiesProto.UserProto;
import ru.hse.server.service.UserService;

import com.google.protobuf.InvalidProtocolBufferException;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/users")
public class UserController {

    static Logger logger = LoggerFactory.getLogger(UserController.class);

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/registration", consumes = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity postUser(@RequestBody UserProto user) {
        try {
            userService.registration(user);
            logger.info("user {} saved", user);
            return ResponseEntity.ok("user saved");
        } catch (EntityExistsException e) {
            logger.error("user {} does not registered, error message: {}", user, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InvalidProtocolBufferException e) {
            logger.error("handle incorrect protobuf {}", user);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/userById", produces = {MediaType.APPLICATION_PROTOBUF_VALUE})
    public ResponseEntity getUserById(@RequestParam Long id) {
        try {
            var user = userService.getUserByID(id);
            logger.debug("find user={} with id={}", user, id);
            return ResponseEntity.ok().body(user);
        } catch (EntityNotFoundException e) {
            logger.error("can not find user with id={}, error message: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/userByLogin", produces = {MediaType.APPLICATION_PROTOBUF_VALUE})
    public ResponseEntity getUserByLogin(@RequestParam String login) {
        try {
            var user = userService.getUserByLogin(login);
            logger.debug("find user={} with login={}", user, login);
            return ResponseEntity.ok().body(user);
        } catch (EntityNotFoundException e) {
            logger.error("can not find user with login={}, error message: {}", login, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/userById")
    public ResponseEntity deleteUserById(@RequestParam Long id) {
        try {
            userService.deleteUserById(id);
            logger.info("user with id={} deleted", id);
            return ResponseEntity.ok().body("user deleted");
        } catch (EntityNotFoundException e) {
            logger.error("can not delete user with id={}, error message: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }
}
