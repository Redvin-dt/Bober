package ru.hse.server.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.server.service.InviteService;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/invitations")
public class InviteController {
    private final Logger logger = LoggerFactory.getLogger(InviteController.class);


    private final InviteService inviteService;

    @PostMapping("/invite")
    public ResponseEntity inviteUser(@RequestParam Long userId, @RequestParam Long groupId) {
        try {
            inviteService.createInvite(userId, groupId);
            return ResponseEntity.ok().body("user successfully invited");
        } catch (EntityNotFoundException e) {
            logger.error("error while invite user", e);
            return ResponseEntity.badRequest().body("error while invite user, error message: " + e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.internalServerError().body("unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/accept/user")
    public ResponseEntity acceptInvite(@RequestParam Long userId, @RequestParam Long groupId) {
        try {
            inviteService.acceptInvite(userId, groupId);
            return ResponseEntity.ok().body("user now member of group"); // TODO: rewrite message
        } catch (EntityNotFoundException e) {
            logger.error("error while invite user", e);
            return ResponseEntity.badRequest().body("error while invite user, error message: " + e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.internalServerError().body("unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/decline/user")
    public ResponseEntity declineInvite(@RequestParam Long userId, @RequestParam Long groupId) {
        try {
            inviteService.declineInvite(userId, groupId);
            return ResponseEntity.ok().body("user now member of group"); // TODO: rewrite message
        } catch (EntityNotFoundException e) {
            logger.error("error while invite user", e);
            return ResponseEntity.badRequest().body("error while invite user, error message: " + e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.internalServerError().body("unexpected error: " + e.getMessage());
        }
    }
}
