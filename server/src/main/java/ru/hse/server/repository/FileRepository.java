package ru.hse.server.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Repository
public interface FileRepository {
    void save(MultipartFile file, String name) throws IOException;

    byte[] get(String name) throws IOException;
}
