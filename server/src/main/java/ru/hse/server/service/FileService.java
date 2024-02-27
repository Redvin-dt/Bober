package ru.hse.server.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.exception.FileValidationException;
import ru.hse.server.repository.FileLocalRepository;
import ru.hse.server.repository.FileRepository;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    FileRepository fileRepository = new FileLocalRepository();
    FileValidationService fileValidator;

    private FileService(@Qualifier("requestFileValidationService") FileValidationService fileValidator) {
        this.fileValidator = fileValidator;
    }

    public String save(MultipartFile file) throws IOException, FileValidationException {
        var validatorResult = fileValidator.validate(file);
        if (validatorResult.notValid()) {
            throw new FileValidationException(validatorResult.getErrorMessage());
        }

        var additionalName = UUID.randomUUID().toString();
        var newFileName = additionalName + "_" + file.getName();
        fileRepository.save(file, newFileName);
        return newFileName;
    }

    public byte[] getFile(String name) throws IOException {
        return fileRepository.get(name);
    }
}
