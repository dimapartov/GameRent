package org.example.gamerent.util.validation.offer_image_file;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageFileValidator.class)
public @interface ImageFile {

    String message() default "Недопустимый формат файла. Только JPEG и PNG.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] types() default {"image/jpeg", "image/png"};

}