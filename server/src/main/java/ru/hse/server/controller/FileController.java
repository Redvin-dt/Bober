package ru.hse.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.service.FileService;

import java.io.*;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService = new FileService();

    @PostMapping
    public ResponseEntity<String> loadFile(@RequestParam MultipartFile file) {
        try {
            return ResponseEntity.ok().body(fileService.save(file));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<InputStream> getFile(@RequestParam String fileName) { // TODO: rewrite
        try {
            return ResponseEntity.ok().body(new FileInputStream(fileService.getFile(fileName)));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new StringBufferInputStream(e.getMessage()));
        }
    }
}
