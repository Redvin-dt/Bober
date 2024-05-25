package ru.hse.server.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.server.exception.AccessException;
import ru.hse.server.exception.EntityUpdateException;
import ru.hse.server.proto.EntitiesProto.GroupModel;
import ru.hse.server.service.GroupService;

@RestController
@RequestMapping("/groups")
public class GroupController {

    static final private Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity createGroup(@RequestBody GroupModel group) {
        try {
            var savedGroup = groupService.createGroup(group);
            logger.info("created group={}", group);
            return ResponseEntity.ok(savedGroup);
        } catch (EntityNotFoundException e) {
            logger.error("can not find models with required field", e);
            return ResponseEntity.badRequest().body("error while creating group: " + e.getMessage());
        } catch (InvalidProtocolBufferException e) {
            logger.error("incorrect protobuf type", e);
            return ResponseEntity.badRequest().body("error while creating group: " + e.getMessage());
        }
    }

    @GetMapping(value = "/groupById", produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity getGroup(@RequestParam Long id) {
        try {
            var group = groupService.findGroupById(id);
            return ResponseEntity.ok().body(group);
        } catch (EntityNotFoundException e) {
            logger.error("can not get group with id={}, since that group does not exists", id);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/groupByName",produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity getGroupByName(@RequestParam String groupName) {
        try {
            var group = groupService.findGroupsByName(groupName);
            return ResponseEntity.ok().body(group);
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/groupByPrefixName",produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity getGroupByPrefixName(@RequestParam String groupName) {
        try {
            var group = groupService.findGroupsByPrefixOfName(groupName);
            return ResponseEntity.ok().body(group);
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/enter", consumes = {MediaType.APPLICATION_PROTOBUF_VALUE})
    public ResponseEntity enterGroup(@RequestBody GroupModel group, @RequestParam Long userId) {
        try {
            groupService.enterGroup(userId, group);
            return ResponseEntity.ok().body("user now member of group");
        } catch (InvalidProtocolBufferException e) {
            logger.error("invalid protocol buffer in enter request", e);
            return ResponseEntity.badRequest().body("invalid protobuf, error: " + e.getMessage());
        } catch (AccessException e) {
            logger.error("failed to access group");
            return ResponseEntity.badRequest().body("invalid group id or password");
        } catch (EntityNotFoundException e) {
            logger.error("failed while find user or group in enter request", e);
            return ResponseEntity.badRequest().body("invalid user id or group id");
        } catch (Exception e) {
            logger.error("unexpected error in enter request", e);
            return ResponseEntity.badRequest().body("unexpected error");
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
