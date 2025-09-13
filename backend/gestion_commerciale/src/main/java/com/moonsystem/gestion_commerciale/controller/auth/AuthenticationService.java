package com.moonsystem.gestion_commerciale.controller.auth;

import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.exception.InvalidOperationException;
import com.moonsystem.gestion_commerciale.model.User;
import com.moonsystem.gestion_commerciale.model.enums.Role;
import com.moonsystem.gestion_commerciale.repository.UserRepository;
import com.moonsystem.gestion_commerciale.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequest request){
        Optional<User> user = userRepository.findByLogin(request.getUsername());
              if(user.isPresent()){
                  throw new InvalidOperationException(
                          "Utilisateur déjà existe",
                          ErrorCodes.USER_NOT_FOUND,
                          List.of("Utilisateur avec l'id " + request.getUsername() + " introuvable")
                          );
              }


        Role userRole = (request.getRole() != null) ? request.getRole() : Role.ADMIN;

        var newUser= User.builder()
                .login(request.getUsername())
                .pass(passwordEncoder.encode(request.getPassword()))
                .gsm(request.getGsm())
                .etat(true)
                .role(userRole)
                .build();

        userRepository.save(newUser);




}
    public AuthenticationResponse authenticate(AuthenticationRequest request){

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            var user =userRepository.findByLogin(request.getUsername()).orElseThrow(
                    ()->new EntityNotFoundException("User not found", List.of("User not found"),ErrorCodes.USER_NOT_FOUND));


            String jwtToken=jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }catch(DisabledException e){
            throw new InvalidOperationException(
                    "Utilisateur désactivé",
                    ErrorCodes.USER_DISABLED,
                    List.of("Le compte est désactivé. Contactez l'administrateur.")
            );
        }
    }


}
