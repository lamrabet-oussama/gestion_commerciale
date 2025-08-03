package com.moonsystem.gestion_commerciale.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueNameValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueName {
    String message() default "Ce nom doit être unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
