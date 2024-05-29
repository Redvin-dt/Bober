package ru.hse.server.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.exception.DeleteFileException;
import ru.hse.server.exception.FileValidationException;
import ru.hse.server.repository.FileRepository;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FileValidationService fileValidator;

    private FileService(@Qualifier("requestFileValidationService") FileValidationService fileValidator, FileRepository fileRepository) {
        this.fileValidator = fileValidator;
        this.fileRepository = fileRepository;
    }

    public String save(MultipartFile file) throws IOException, FileValidationException {
        var validatorResult = fileValidator.validate(file);
        if (validatorResult.notValid()) {
            throw new FileValidationException(validatorResult.getErrorMessage());
        }

        var additionalName = UUID.randomUUID().toString();
        var newFileName = additionalName + "_" + file.getOriginalFilename();
        fileRepository.save(file, newFileName);
        return newFileName;
    }

    public Resource getFile(String name) throws IOException {
        return fileRepository.get(name);
    }

    public void deleteFile(String name) throws IOException, DeleteFileException {
        fileRepository.delete(name);
    }
}
