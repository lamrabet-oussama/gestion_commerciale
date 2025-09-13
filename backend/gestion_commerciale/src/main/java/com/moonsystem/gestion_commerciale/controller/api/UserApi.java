package com.moonsystem.gestion_commerciale.controller.api;

import java.util.List;

import com.moonsystem.gestion_commerciale.dto.UpdateUserRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import com.moonsystem.gestion_commerciale.dto.UserDto;
import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserApi {

    @GetMapping(value = APP_ROOT + "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Lister les utilisateurs disponibles",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Liste des Utilisateurs",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class)) // <- return type: List<UserDto>
                            )
                    )
            }
    )
    List<UserDto> getAllUsers();

    @GetMapping(value = APP_ROOT + "/users/current", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Récupérer l'utilisateur actuel",
            description = "Retourne les informations de l'utilisateur actuellement connecté au système.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Utilisateur actuel récupéré avec succès",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto.class) // <- return type: UserDto
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
                    @ApiResponse(responseCode = "403", description = "Accès refusé")
            }
    )
    UserDto getCurrentUser();

    /**
     * Bloquer un utilisateur spécifique par son ID.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(APP_ROOT + "/bloquer/{userId}")
    @Operation(
            summary = "Bloquer un utilisateur",
            description = "Permet à un administrateur de bloquer un utilisateur afin de lui interdire l'accès au système.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Utilisateur bloqué avec succès",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string", example = "Utilisateur bloqué avec succès") // <- return type: ResponseEntity<String>
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
                    @ApiResponse(responseCode = "403", description = "Accès refusé")
            }
    )
    ResponseEntity<String> bloquerUser(@PathVariable Integer userId);

    /**
     * Débloquer un utilisateur spécifique par son ID.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(APP_ROOT + "/debloquer/{userId}")
    @Operation(
            summary = "Débloquer un utilisateur",
            description = "Permet à un administrateur de rétablir l'accès d'un utilisateur précédemment bloqué.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Utilisateur débloqué avec succès",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string", example = "Utilisateur débloqué avec succès") // <- return type: ResponseEntity<String>
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
                    @ApiResponse(responseCode = "403", description = "Accès refusé")
            }
    )
    ResponseEntity<String> debloquerUser(@PathVariable Integer userId);

    /**
     * Mettre à jour un utilisateur spécifique par son ID.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(APP_ROOT + "/update-ser/{userId}")
    @Operation(
            summary = "Mettre à jour un utilisateur",
            description = "Permet à un administrateur de mettre à jour les informations d'un utilisateur existant.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Utilisateur mis à jour avec succès",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto.class) // <- return type: ResponseEntity<UserDto>
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
                    @ApiResponse(responseCode = "403", description = "Accès refusé")
            }
    )
    ResponseEntity<UserDto> updateUser(
            @PathVariable Integer userId,
            @RequestBody UpdateUserRequest request
    );
}
