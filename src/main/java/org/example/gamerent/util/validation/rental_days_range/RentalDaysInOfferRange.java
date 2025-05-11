package org.example.gamerent.util.validation.rental_days_range;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RentalDaysInOfferRangeValidator.class)
public @interface RentalDaysInOfferRange {

    String message() default "Некорректное количество дней аренды";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}