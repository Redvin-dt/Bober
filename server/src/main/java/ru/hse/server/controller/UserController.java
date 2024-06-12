package ru.hse.server.controller;

import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import ru.hse.server.exception.AccessException;
import ru.hse.server.exception.EntityUpdateException;
import ru.hse.server.proto.EntitiesProto.UserModel;
import ru.hse.server.service.AuthService;
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
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    final static private int UNAUTHORIZED_STATUS = 401;

    static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AuthService authService;

    @PostMapping(value = "/registration", consumes = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}, produces = {MediaType.APPLICATION_PROTOBUF_VALUE})
    public ResponseEntity registrationUser(@RequestBody UserModel user) {
        try {
            var registeredUser = userService.registration(user);
            registeredUser = registeredUser.toBuilder().setAccessToken(authService.getAccessToken(user)).build();
            logger.info("user {} saved", registeredUser);
            return ResponseEntity.ok(registeredUser);
        } catch (EntityExistsException e) {
            logger.error("user {} does not registered, error message: {}", user, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InvalidProtocolBufferException e) {
            logger.error("handle incorrect protobuf {}", user);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (AuthException e) {
            logger.error("can not generate user token", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}, produces = {MediaType.APPLICATION_PROTOBUF_VALUE})
    public ResponseEntity loginUser(@RequestBody UserModel user) {
        try {
            return ResponseEntity.ok().body(authService.login(user));
        } catch (AuthException e) {
            logger.error("incorrect user info, request login user={}", user, e);
            return ResponseEntity.status(UNAUTHORIZED_STATUS).body(e.getMessage());
        }
    }

    @PostMapping(value = "/userPassedTest", produces = {MediaType.APPLICATION_PROTOBUF_VALUE})
    public ResponseEntity userPassedTest(@RequestParam Long userId, @RequestParam Long testId,
                                         @RequestParam Long chapterId, @RequestParam String testName,
                                         @RequestParam String chapterName, @RequestParam Long rightAns, @RequestParam Long questNum) {
        try {
            userService.addPassedTestToUser(testId, chapterId, testName, chapterName, userId, rightAns, questNum);
            return ResponseEntity.ok().body(userService.getUserByID(userId));
        } catch (EntityNotFoundException e) {
            logger.error("error while adding test to user", e);
            return ResponseEntity.badRequest().body("error while adding test to user, error message: " + e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.internalServerError().body("unexpected error: " + e.getMessage());
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

    @GetMapping(value = "/userByEmail", produces = {MediaType.APPLICATION_PROTOBUF_VALUE})
    public ResponseEntity getUserByEmail(@RequestParam String email) {
        try {
            var user = userService.getUserByEmail(email);
            logger.debug("find user={} with email={}", user, email);
            return ResponseEntity.ok().body(user);
        } catch (EntityNotFoundException e) {
            logger.error("can not find user with email={}, error message: {}", email, e.getMessage());
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

