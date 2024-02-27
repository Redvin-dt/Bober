package ru.hse.server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.utils.ValidationResult;

@Service
public interface FileValidationService {
    ValidationResult validate(MultipartFile file);
}
