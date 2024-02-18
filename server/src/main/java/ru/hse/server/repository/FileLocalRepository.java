package ru.hse.server.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileLocalRepository implements FileRepository {

    private File tempDirectory;

    @Override
    public void save(MultipartFile file, String name) throws IOException {
        if (tempDirectory == null){
            createTempDirectory();
        }

        file.transferTo(Path.of(tempDirectory.getAbsolutePath() + name));
    }

    @Override
    public byte[] get(String name) throws IOException {
        if (tempDirectory == null){
            createTempDirectory();
        }

        File file = new File(tempDirectory.getAbsolutePath() + name);
        if (file.exists()){
            return Files.readAllBytes(file.toPath());
        }

        throw new FileNotFoundException("File " + name + "not found");
    }

    private void createTempDirectory() throws IOException {
        tempDirectory = Files.createTempDirectory(UUID.randomUUID().toString() + "_" + "tempDir").toFile();
        if (!tempDirectory.exists()){
            throw new FileNotFoundException("Temporary directory does not created");
        }
    }
}
