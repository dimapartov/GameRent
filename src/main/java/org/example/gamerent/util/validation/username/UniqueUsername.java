package org.example.gamerent.util.validation.username;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {

    String message() default "Имя пользователя уже занято";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}