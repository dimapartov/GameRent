package org.example.gamerent.util.validation.offer_image_file;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;


public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {

    private String[] allowedTypes;

    @Override
    public void initialize(ImageFile constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.types();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        String contentType = file.getContentType();
        if (contentType != null && Arrays.asList(allowedTypes).contains(contentType)) {
            return true;
        }
        String filename = file.getOriginalFilename();
        if (filename != null) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")) {
                return true;
            }
        }
        return false;
    }

}