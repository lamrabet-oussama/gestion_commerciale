package com.moonsystem.gestion_commerciale.services.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.moonsystem.gestion_commerciale.dto.UpdateUserRequest;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.exception.InvalidOperationException;
import com.moonsystem.gestion_commerciale.model.User;
import com.moonsystem.gestion_commerciale.model.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.moonsystem.gestion_commerciale.dto.UserDto;
import com.moonsystem.gestion_commerciale.repository.UserRepository;
import com.moonsystem.gestion_commerciale.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override

    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidOperationException(
                    "Aucun utilisateur connecté",
                    ErrorCodes.BAD_CREDENTIALS,
                    Collections.singletonList("L'utilisateur n'est pas authentifié")
            );
        }

         User currentUser=(User) authentication.getPrincipal();
        return UserDto.fromEntity(currentUser);
    }

    @Override
    public void bloquerDebloquerUser(Integer userId, boolean etat) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Utilisateur non trouvé",
                        List.of("Aucun utilisateur trouvé avec l'id " + userId),
                        ErrorCodes.USER_NOT_FOUND
                ));

        user.setEtat(etat);
        userRepository.save(user);
    }

    @Override
    public UserDto updateUser(Integer userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Utilisateur non trouvé",
                        List.of("Utilisateur avec l'id " + userId + " introuvable"),
                        ErrorCodes.USER_NOT_FOUND
                ));

        boolean isModified = false;

        // Mise à jour du login
        if (request.getLogin() != null && !request.getLogin().isBlank()) {
            if (!request.getLogin().equals(user.getLogin())) {
                boolean userExisted=userRepository.existsByLogin(request.getLogin());
                if (userExisted) {
                    throw new InvalidOperationException(
                            "Ce nom d'utilisateur existe déjà",
                            ErrorCodes.USER_ALREADY_EXISTS,
                            List.of("Le username '" + request.getLogin() + "' est déjà pris")
                    );
                }
                user.setLogin(request.getLogin());
                isModified = true;
            }
        }

        // Mise à jour du GSM
        if (request.getGsm() != null && !request.getGsm().isBlank()) {
            if (!request.getGsm().equals(user.getGsm())) {
                user.setGsm(request.getGsm());
                isModified = true;
            }
        }

        // Mise à jour du mot de passe
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPass(passwordEncoder.encode(request.getPassword()));
            isModified = true;
        }

        // Mise à jour du rôle
        if (request.getRole() != null) {
            Role newRole = Role.valueOf(request.getRole());
            if (!newRole.equals(user.getRole())) {
                user.setRole(newRole);
                isModified = true;
            }
        }

        if (isModified) {
            User savedUser = userRepository.save(user);
            return UserDto.fromEntity(savedUser);
        }

        // Retourner l'utilisateur existant si aucune modification
        return UserDto.fromEntity(user);
    }
}
