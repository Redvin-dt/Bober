package ru.hse.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.exception.FileValidationException;
import ru.hse.server.repository.FileRepository;
import ru.hse.server.service.FileService;

import java.io.*;
import java.util.Random;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FileServiceTest {
    private static final String NON_EXISTING_EXTENSION = "nonExistingExtension";

    @Autowired
    private FileService fileService;

    @MockBean
    private FileRepository fileRepository;

    @Value("${fileValidator.supportedExtensions}")
    private String[] supportedExtensions;
    @Value("${fileValidator.maxFileSizeBytes}")
    private long maxFileSizeBytes;

    private static Random random;

    @BeforeAll
    public static void init() {
        random = new Random();
    }

    @Test
    public void basicSaveLoadTest() throws IOException, FileValidationException {
        for (String extension : supportedExtensions) {
            MultipartFile multipartFile = createFile(random.nextLong(maxFileSizeBytes), extension);
            fileService.save(multipartFile);
            verify(fileRepository, times(1)).save(any(), any());
        }
    }

    @Test
    public void checkFileSizeTest() throws IOException {
        for (String extension : supportedExtensions) {
            MultipartFile multipartFile = createFile(random.nextLong(100) + maxFileSizeBytes + 1, extension);
            Assertions.assertThrows(FileValidationException.class, () -> fileService.save(multipartFile));
            verify(fileRepository, never()).save(any(), any());
        }
    }

    @Test
    public void checkFileExtensionTest() throws IOException {
        MultipartFile multipartFile = createFile(random.nextLong(maxFileSizeBytes), NON_EXISTING_EXTENSION);
        Assertions.assertThrows(FileValidationException.class, () -> fileService.save(multipartFile));
        verify(fileRepository, never()).save(any(), any());
    }

    private MultipartFile createFile(long fileSizeBytes, String extension) {
        String fileName = UUID.randomUUID() + "." + extension;
        MultipartFile file = Mockito.mock(MultipartFile.class);

        doReturn(fileName).when(file).getName();
        doReturn(fileName).when(file).getOriginalFilename();
        doReturn(fileSizeBytes).when(file).getSize();

        return file;
    }
}

