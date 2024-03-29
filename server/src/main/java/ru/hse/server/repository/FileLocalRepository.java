package ru.hse.server.repository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Repository
public class FileLocalRepository implements FileRepository {

    private File tempDirectory;

    @Override
    public void save(MultipartFile file, String name) throws IOException {
        if (tempDirectory == null) {
            createTempDirectory();
        }

        file.transferTo(Path.of(tempDirectory.getAbsolutePath() + name));
    }

    @Override
    public Resource get(String name) throws IOException {
        if (tempDirectory == null) {
            createTempDirectory();
        }

        File file = new File(tempDirectory.getAbsolutePath() + name);
        if (file.exists()) {
            return new UrlResource(file.toURI());
        }

        throw new FileNotFoundException("File " + name + "not found");
    }

    private void createTempDirectory() throws IOException {
        tempDirectory = Files.createTempDirectory(UUID.randomUUID() + "_" + "tempDir").toFile();
        // TODO: do create regular director
        if (!tempDirectory.exists()) {
            throw new FileNotFoundException("Temporary directory does not created");
        }
    }
}
