package ru.hse.server.service;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.database.entities.Chapter;
import ru.hse.server.exception.DeleteFileException;
import ru.hse.server.exception.FileValidationException;
import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.repository.ChapterRepository;
import ru.hse.server.repository.FileRepository;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FileValidationService fileValidator;
    private final ChapterRepository chapterRepository;

    private FileService(@Qualifier("requestFileValidationService") FileValidationService fileValidator, FileRepository fileRepository, ChapterRepository chapterRepository) {
        this.fileValidator = fileValidator;
        this.fileRepository = fileRepository;
        this.chapterRepository = chapterRepository;
    }

    public void addFileToChapter(MultipartFile file, Long chapterId) throws IOException,
            FileValidationException, EntityNotFoundException {
        var fileName = save(file);

        var chapterOption = chapterRepository.findById(chapterId);
        if (chapterOption.isEmpty()) {
            throw new EntityNotFoundException("can not find group with that id");
        }

        var chapter = chapterOption.get();
        chapter.setTestData(fileName);
        chapterRepository.update(chapter);
    }

    public String save(MultipartFile file) throws IOException, FileValidationException {
        var validatorResult = fileValidator.validate(file);
        if (validatorResult.notValid()) {
            throw new FileValidationException(validatorResult.getErrorMessage());
        }

        var newFileName = generateNewFileName(file);
        fileRepository.save(file, newFileName);
        return newFileName;
    }

    public Resource getFile(String name) throws IOException {
        return fileRepository.get(name);
    }

    public void deleteFile(String name) throws IOException, DeleteFileException {
        fileRepository.delete(name);
    }

    private String generateNewFileName(MultipartFile file) {
        var additionalName = UUID.randomUUID().toString();
        return additionalName + "_" + file.getOriginalFilename();
    }
}
