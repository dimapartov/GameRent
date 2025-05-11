package org.example.gamerent.util.validation.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {

    String message() default "На данную почту уже зарегистрирован аккаунт";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}