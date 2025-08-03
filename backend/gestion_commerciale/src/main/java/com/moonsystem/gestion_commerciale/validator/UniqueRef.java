package com.moonsystem.gestion_commerciale.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueRefValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueRef {
    String message() default "La référence doit être unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
