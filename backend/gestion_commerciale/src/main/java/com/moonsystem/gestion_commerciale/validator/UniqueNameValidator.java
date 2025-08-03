package com.moonsystem.gestion_commerciale.validator;

import com.moonsystem.gestion_commerciale.repository.TierRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

    private final TierRepository tierRepository;

    // Injection du repository via constructeur
    public UniqueNameValidator(TierRepository tierRepository) {
        this.tierRepository = tierRepository;
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return true; // Ou false selon logique (ici on laisse à @NotNull gérer)
        }
        // Vérifie si ref existe déjà en base
        return !tierRepository.existsByNom(name);
    }
}
