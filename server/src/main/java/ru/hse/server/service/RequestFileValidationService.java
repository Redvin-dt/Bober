package ru.hse.server.service;

import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.server.utils.ValidationResult;
import ru.hse.server.utils.ValidationStep;

import java.util.Arrays;

@Service
public class RequestFileValidationService implements FileValidationService {

    @Value("${fileValidator.supportedExtensions}")
    private String[] supportedExtensions;

    @Value("${fileValidator.maxFileSizeBytes}")
    private long maxFileSizeBytes;

    @Override
    public ValidationResult validate(MultipartFile file) {
        return new FileSizeValidator().addStep(new FileExtensionValidator()).validate(file);
    }

    @NoArgsConstructor
    private class FileSizeValidator extends ValidationStep<MultipartFile> {
        @Override
        public ValidationResult validate(MultipartFile validatedValue) {
            if (validatedValue.getSize() > maxFileSizeBytes) {
                return ValidationResult.invalid("File too large, maximum size is " + maxFileSizeBytes + " bytes, actual " + validatedValue.getSize() + " bytes");
            }

            return checkNext(validatedValue);
        }
    }

    @NoArgsConstructor
    private class FileExtensionValidator extends ValidationStep<MultipartFile> {
        @Override
        public ValidationResult validate(MultipartFile validatedValue) {
            String extension = FilenameUtils.getExtension(validatedValue.getOriginalFilename());
            if (extension == null) {
                return ValidationResult.invalid("Can not identify file extension");
            }

            if (!Arrays.asList(supportedExtensions).contains(extension)) {
                return ValidationResult.invalid("Not supported extension " + extension);
            }

            return checkNext(validatedValue);
        }
    }
}
