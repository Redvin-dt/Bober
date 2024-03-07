package ru.hse.server.controller;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.database.entities.Group;
import ru.hse.server.service.GroupService;

@RestController
@RequestMapping("/groups")
public class GroupController {

    static final private Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<String> createGroup(@RequestParam Group group) {
        groupService.createGroup(group);
        logger.info("created group={}", group);
        return ResponseEntity.ok("group successfully created");
    }

    @GetMapping
    public ResponseEntity getGroup(@RequestParam Long id) {
        try {
            var group = groupService.findGroupById(id);
            return ResponseEntity.ok().body(group);
        } catch (EntityNotFoundException e) {
            logger.error("can not get grop with id={}, since that group does not exists", id);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity deleteGroup(@RequestParam Long id) {
        try {
            groupService.deleteGroup(id);
            return ResponseEntity.ok().body("group successfully deleted");
        } catch (EntityNotFoundException e) {
            logger.error("can not delete group with id={}, since that group does not exists", id);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
