package ru.hse.server.repository;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.exception.DeleteFileException;

import java.io.IOException;

@Repository
public interface FileRepository {
    void save(MultipartFile file, String name) throws IOException;
    Resource get(String name) throws IOException;
    void delete(String name) throws IOException, DeleteFileException;
}
