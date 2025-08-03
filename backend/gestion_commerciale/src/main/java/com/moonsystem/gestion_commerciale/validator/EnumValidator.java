package com.moonsystem.gestion_commerciale.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // ou false selon ta logique

        for (Enum<?> enumVal : enumClass.getEnumConstants()) {
            if (enumVal.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
