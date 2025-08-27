package com.moonsystem.gestion_commerciale.controller.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.moonsystem.gestion_commerciale.dto.TierDto;
import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;
import com.moonsystem.gestion_commerciale.utils.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

public interface TierApi {

    @PostMapping(value = APP_ROOT + "/tiers/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)

    @Operation(
            summary = "Créer un nouvel tier",
            description = "Permet de créer un nouvel tier dans le système de gestion commerciale"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Tier créé avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = TierDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Données d'entrée invalides",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Tier avec ce code existe déjà",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    TierDto createTier(@Valid @RequestBody TierDto tierDto);

    @PutMapping(value = APP_ROOT + "/tiers/update", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Modifier un tier",
            description = "Permet de modifier un  tier dans le système de gestion commerciale"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Tier modifié avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = TierDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Données d'entrée invalides",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Tier avec ce code existe déjà",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    TierDto updateTier(@Valid @RequestBody TierDto tierDto);

    @GetMapping(value = APP_ROOT + "/tiers/types", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Lister les types de tier disponibles",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Liste des Types de Tier",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                array = @ArraySchema(schema = @Schema(implementation = String.class))
                        )
                )
            }
    )
    List<String> getAllTierType();

    @GetMapping(value = APP_ROOT + "/tiers/villes", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Listerles villes disponibles pour les tiers",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Liste des Villes",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                array = @ArraySchema(schema = @Schema(implementation = String.class))
                        )
                )
            }
    )
    List<String> getAllTierVilles();

    @GetMapping(value = APP_ROOT + "/tiers/{id}")
    @Operation(
            summary = "Récupérer un article par son code",
            description = "Permet de récupérer les détails d'un article spécifique en utilisant son code unique"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Tier trouvé avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = TierDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Tier non trouvé",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    TierDto findById(@PathVariable("id") int id);

    @DeleteMapping(value = APP_ROOT + "/tiers/delete/{id}")
    @Operation(
            summary = "Supprimer un article",
            description = "Permet de supprimer définitivement un article du système en utilisant son code"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Tier supprimé avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = Boolean.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Tier non trouvé",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Impossible de supprimer le tier (contraintes référentielles)",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    boolean deleteTier(@PathVariable("id") int id);

    @GetMapping(value = APP_ROOT + "/tiers/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Rechercher des tiers",
            description = "Permet de rechercher des tiers en utilisant un mot-clé qui sera appliqué sur différents champs "
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Tiers paginés récupérés avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = PageResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Mot-clé de recherche invalide",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    PageResponse<TierDto> search(@RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size);

    @GetMapping(value = APP_ROOT + "/tiers/number", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Nombre total de tiers",
            description = "Permet de retourner le nombre total de tiers dans le système"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Nombre bien récupéré",
            content = @Content
    )
    Integer numberOfTiers();


    @GetMapping(value = APP_ROOT + "/tiers/client")
    @Operation(
            summary = "Récupérer tous les clients",
            description = "Retourne la liste de tous les tiers ayant le rôle CLIENT."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des clients récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TierDto.class))
            )
    )
    List<TierDto> getAllClient();

    @GetMapping(value = APP_ROOT + "/tiers/fournisseur")
    @Operation(
            summary = "Récupérer tous les fournisseurs",
            description = "Retourne la liste de tous les tiers ayant le rôle FOURNISSEUR."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des fournisseurs récupérée avec succès",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TierDto.class))
            )
    )
    List<TierDto> getAllFournisseur();

}
