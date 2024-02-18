package ru.hse.server.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileRepository {
    void save(MultipartFile file, String name) throws IOException; // TODO: mb set usual file

     File get(String name) throws IOException;
}
