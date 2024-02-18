package ru.hse.server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.repository.FileLocalRepository;
import ru.hse.server.repository.FileRepository;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    FileRepository fileRepository = new FileLocalRepository();

    public String save(MultipartFile file) throws IOException {
        var additionalName = UUID.randomUUID().toString();
        var newFileName = additionalName + "_" + file.getName();
        fileRepository.save(file, newFileName);
        return newFileName;
    }

    public byte[] getFile(String name) throws IOException {
        return fileRepository.get(name);
    }
}
