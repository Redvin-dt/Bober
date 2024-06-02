package ru.hse.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.exception.DeleteFileException;
import ru.hse.server.exception.FileValidationException;
import ru.hse.server.service.FileService;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {

    static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;

    private FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<String> loadFileToChapter(@RequestParam MultipartFile file, @RequestParam Long chapterId) {
        try {
            fileService.addFileToChapter(file, chapterId);
            logger.info("File {} successful loaded", file.getOriginalFilename());
            return ResponseEntity.ok().body("File " + file.getOriginalFilename() + " saved to chapter " + chapterId);
        } catch (IOException e) {
            logger.error("Can not load file {}, error message: {}", file.getOriginalFilename(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (FileValidationException e) {
            logger.error("Invalid file {}, error message: {}", file.getOriginalFilename(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Resource> getFile(@RequestParam String fileName) {
        try {
            var response = ResponseEntity.ok().body(fileService.getFile(fileName));
            logger.debug("File with fileName={} successful gets", fileName);
            return response;
        } catch (IOException e) {
            logger.error("Can not get file with fileName={}, error message: {}", fileName, e.getMessage());
            return ResponseEntity.badRequest().body(new ByteArrayResource(e.getMessage().getBytes()));
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            return ResponseEntity.badRequest().body(new ByteArrayResource(e.getMessage().getBytes()));
        }
    }

    @DeleteMapping
    public ResponseEntity deleteFile(@RequestParam String fileName) {
        try {
            fileService.deleteFile(fileName);
            return ResponseEntity.ok().body("successfully delete file " + fileName);
        } catch (IOException e) {
            logger.error("Can not get file with fileName={}, error message: {}", fileName, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DeleteFileException e) {
            logger.error("Can not dlete file with fileName={}, error message: {}", fileName, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            return ResponseEntity.badRequest().body(new ByteArrayResource(e.getMessage().getBytes()));
        }
    }
}
