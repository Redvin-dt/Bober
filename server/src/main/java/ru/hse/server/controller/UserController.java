package ru.hse.server.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.hse.database.entities.User;

import org.springframework.http.ResponseEntity;
import ru.hse.protobuf.entities.ProtoUser;
import ru.hse.server.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UserController {

    static Logger logger = LoggerFactory.getLogger(UserController.class);

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/registration", consumes = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity postUser(@RequestBody ProtoUser.User puser) {
        try {
            User user = new User(puser.getName(), puser.getPassword());
            System.err.println(puser.toString()); // TODO: remove
            userService.registration(user);
            logger.info("User {} saved", user);
            return ResponseEntity.ok("User saved");
        } catch (EntityExistsException e) {
            logger.error("User {} does not registered, error message: {}", puser, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/userById", produces = {MediaType.APPLICATION_PROTOBUF_VALUE})
    public ResponseEntity getUserById(@RequestParam Long id) {
        try {
            var user = userService.getUserByID(id);
            logger.debug("Find user={} with id={}", user, id);
            var responseBuilder = ProtoUser.User.newBuilder();
            responseBuilder.setId(user.getUserId());
            responseBuilder.setName(user.getUserLogin());
            responseBuilder.setPassword(user.getPasswordHash());

            return ResponseEntity.ok().body(responseBuilder.build());
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


    private static Message fromJson(String json) throws IOException {
        Message.Builder structBuilder = Struct.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder);
        return structBuilder.build();
    }
}
