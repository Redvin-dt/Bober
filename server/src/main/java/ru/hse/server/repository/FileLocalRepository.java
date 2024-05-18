package ru.hse.server.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.exception.DeleteFileException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

@Repository
public class FileLocalRepository implements FileRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileLocalRepository.class);

    private final File directory;


    FileLocalRepository(@Value("${fileLocalRepository.saveFileDir}") String fileDirectory) {
        directory = new File(fileDirectory);

        if (!directory.exists()) {
            logger.error("can not find directory {}", fileDirectory);
            throw new RuntimeException("file directory " + fileDirectory + " does not exist"); // TODO: mb not runtime
        }

        if (!directory.isDirectory()) {
            logger.error("{} is not a directory", fileDirectory);
            throw new RuntimeException(fileDirectory + " is not directory");
        }
    }

    @Override
    public void save(MultipartFile file, String name) throws IOException {
        file.transferTo(Path.of(directory.getAbsolutePath() + File.separator + name));
    }

    @Override
    public Resource get(String name) throws IOException {
        File file = new File(directory.getAbsolutePath() + File.separator + name);
        if (file.exists()) {
            return new UrlResource(file.toURI());
        }

        throw new FileNotFoundException("File " + name + "not found");
    }

    @Override
    public void delete(String name) throws IOException, DeleteFileException {
        File file = new File(directory.getAbsolutePath() + File.separator + name);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + name + "not found");
        }

        if (!file.delete()) {
            throw new DeleteFileException("Can not delete file " + name);
        }
    }
}
