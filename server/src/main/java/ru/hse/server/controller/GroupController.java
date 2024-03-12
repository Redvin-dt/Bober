package ru.hse.server.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping(produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.TEXT_PLAIN_VALUE})
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
