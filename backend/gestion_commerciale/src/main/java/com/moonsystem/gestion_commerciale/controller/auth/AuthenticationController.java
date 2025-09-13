package com.moonsystem.gestion_commerciale.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

@RestController
@RequestMapping(APP_ROOT+"/auth")
@Tag(name = "Authentication", description = "Endpoints pour la gestion de l'authentification et de l'enregistrement des utilisateurs")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @Operation(
            summary = "Enregistrer un nouvel utilisateur",
            description = "Cette méthode permet de créer un nouvel utilisateur dans le système."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur enregistré avec succès",
                    content = @Content(mediaType = "application/json"
                            )),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    @PostMapping("/register")
    public void register(
            @RequestBody RegisterRequest request) {
        System.out.println(request);
         authenticationService.register(request);
    }

    @Operation(
            summary = "Authentifier un utilisateur",
            description = "Cette méthode permet d'authentifier un utilisateur et de retourner un token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }


}
