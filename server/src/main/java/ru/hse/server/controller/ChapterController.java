package ru.hse.server.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.proto.EntitiesProto.ChapterModel;
import ru.hse.server.service.ChapterService;

@RestController
@RequestMapping("/chapters")
public class ChapterController {

    static final private Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<String> createChapter(@RequestBody ChapterModel chapter) {
        try {
            chapterService.createChapter(chapter);
            logger.info("created chapter={}", chapter);
            return ResponseEntity.ok("chapter successfully created");
        } catch (EntityNotFoundException e) {
            logger.error("can not find models with required field", e);
            return ResponseEntity.badRequest().body("error while creating chapter: " + e.getMessage());
        } catch (InvalidProtocolBufferException e) {
            logger.error("incorrect protobuf type", e);
            return ResponseEntity.badRequest().body("error while creating chapter: " + e.getMessage());
        }
    }

    @GetMapping(produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity getChapter(@RequestParam Long id) {
        try {
            var chapter = chapterService.getChapterByID(id);
            return ResponseEntity.ok().body(chapter);
        } catch (EntityNotFoundException e) {
            logger.error("can not get chapter with id={}, since that chapter does not exists", id);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity deleteChapter(@RequestParam Long id) {
        try {
            chapterService.deleteChapter(id);
            return ResponseEntity.ok().body("chapter successfully deleted");
        } catch (EntityNotFoundException e) {
            logger.error("can not delete chapter with id={}, since that chapter does not exists", id);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}