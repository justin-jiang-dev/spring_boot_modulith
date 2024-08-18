package com.nuoson.modulith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Pattern(regexp = "[0-9]*")
@Size(min = 5, max = 5)
@Documented
@Constraint(validatedBy = { CustomZipCodeValidator.class })
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomZipCode {

    String message()

    default "Field must not be null and must be between 5 and 10 characters long";

    Class<?>[] groups() default {
    };

    Class<? extends Payload>[] payload() default {};

}