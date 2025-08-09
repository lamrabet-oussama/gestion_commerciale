package com.moonsystem.gestion_commerciale.validator;

import org.springframework.stereotype.Component;

import com.moonsystem.gestion_commerciale.repository.TierRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueRefValidator implements ConstraintValidator<UniqueRef, Integer> {

    private final TierRepository tierRepository;

    // Injection du repository via constructeur
    public UniqueRefValidator(TierRepository tierRepository) {
        this.tierRepository = tierRepository;
    }

    @Override
    public boolean isValid(Integer ref, ConstraintValidatorContext context) {
        if (ref == null) {
            return true; // Ou false selon logique (ici on laisse à @NotNull gérer)
        }
        // Vérifie si ref existe déjà en base
        return !tierRepository.existsByRef(ref);
    }
}
