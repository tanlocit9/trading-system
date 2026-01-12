package com.aquariux.trading.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidatorImpl.class)
public @interface EnumValidator {
    /**
     * The enum class that defines allowed values for the annotated field.
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * Validation message used when the value is not one of the enum constants.
     */
    String message() default "Invalid value for this field";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}