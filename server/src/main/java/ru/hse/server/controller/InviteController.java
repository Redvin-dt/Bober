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
import ru.hse.server.exception.AccessException;
import ru.hse.server.exception.EntityUpdateException;
import ru.hse.server.service.InviteService;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/invitations")
public class InviteController {
    private final Logger logger = LoggerFactory.getLogger(InviteController.class);


    private final InviteService inviteService;

    @PostMapping("/invite")
    public ResponseEntity inviteUser(@RequestParam String userLogin, @RequestParam Long groupId) {
        try {
            inviteService.createInvite(userLogin, groupId);
            return ResponseEntity.ok().body("user successfully invited");
        } catch (EntityNotFoundException e) {
            logger.error("error while invite user", e);
            return ResponseEntity.badRequest().body("error while invite user, error message: " + e.getMessage());
        } catch (AccessException e) {
            logger.error("user with id={} has no access to group with login={}", userLogin, groupId, e);
            return ResponseEntity.badRequest().body("user has no access for this group: " + e.getMessage());
        } catch (EntityUpdateException e) {
            logger.error("error while update entity", e);
            return ResponseEntity.badRequest().body("error on invite user: " + e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.internalServerError().body("unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/accept/user")
    public ResponseEntity acceptInvite(@RequestParam Long userId, @RequestParam Long groupId) {
        try {
            inviteService.acceptInvite(userId, groupId);
            return ResponseEntity.ok().body("user now member of group");
        } catch (EntityNotFoundException e) {
            logger.error("error while invite user", e);
            return ResponseEntity.badRequest().body("error while invite user, error message: " + e.getMessage());
        } catch (EntityUpdateException e) {
            logger.error("error while update entity", e);
            return ResponseEntity.badRequest().body("error on accept invite: " + e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.internalServerError().body("unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/decline/user")
    public ResponseEntity declineInvite(@RequestParam Long userId, @RequestParam Long groupId) {
        try {
            inviteService.declineInvite(userId, groupId);
            return ResponseEntity.ok().body("invite was canceled");
        } catch (EntityNotFoundException e) {
            logger.error("error while invite user", e);
            return ResponseEntity.badRequest().body("error while invite user, error message: " + e.getMessage());
        } catch (EntityUpdateException e) {
            logger.error("error while update entity", e);
            return ResponseEntity.badRequest().body("error on decline invite: " + e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.internalServerError().body("unexpected error: " + e.getMessage());
        }
    }
}
