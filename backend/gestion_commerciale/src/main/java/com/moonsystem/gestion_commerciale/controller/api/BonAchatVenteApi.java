package com.moonsystem.gestion_commerciale.controller.api;

import com.moonsystem.gestion_commerciale.dto.BonAchatVenteDto;
import com.moonsystem.gestion_commerciale.exception.InvalidOperationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

@Tag(name = "Bons Achat/Vente", description = "API pour gérer les bons d'achat et de vente")
public interface BonAchatVenteApi {

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
            summary = "Créer ou mettre à jour un bon d'achat/vente",
            description = "Si le paramètre 'serie' est fourni, on met à jour le bon correspondant. " +
                    "Sinon, on crée un nouveau bon."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bon créé ou mis à jour avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BonAchatVenteDto.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bon non trouvé (si update)", content = @Content)
    })
    @PostMapping(value = APP_ROOT+"/bonachat")
     BonAchatVenteDto createBonAchat(
            @RequestBody BonAchatVenteDto dto
    );

    @Operation(
            summary = "Créer ou mettre à jour un bon d'achat/vente",
            description = "Si le paramètre 'serie' est fourni, on met à jour le bon correspondant. " +
                    "Sinon, on crée un nouveau bon."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bon créé ou mis à jour avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BonAchatVenteDto.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bon non trouvé (si update)", content = @Content)
    })

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping(value = APP_ROOT+"/bonachat/update/{serie}")
    BonAchatVenteDto updateBonAchat(
            @RequestBody BonAchatVenteDto dto,
            @Parameter(description = "Série du bon (obligatoire uniquement pour la mise à jour)")
            @PathVariable(value = "serie", required = false) String serie
    );

    @Operation(summary = "Récupérer un bon d'achat", description = "Retourne les détails d'un bon d'achat selon la série et le code utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bon d'achat trouvé",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BonAchatVenteDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Bon d'achat non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping(value = APP_ROOT + "/bonachat", produces = MediaType.APPLICATION_JSON_VALUE)
    BonAchatVenteDto getBonAchat(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam(value = "userCod", required = false) Integer userCod,
            @Parameter(description = "Série du bon à rechercher")
            @RequestParam(value = "serie",required = false) String serie
    );

    @Operation(summary = "Télécharger un bon d'achat en PDF", description = "Retourne le fichier PDF du bon d'achat correspondant à la série donnée.")
    @ApiResponse(
            responseCode = "200",
            description = "Fichier PDF du bon d'achat",
            content = @Content(mediaType = "application/pdf")
    )

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping(value = APP_ROOT + "/bonachat/download", produces = "application/pdf")
    ResponseEntity<byte[]> downloadBonAchat(

            @Parameter(description = "Série du bon à télécharger")
            @RequestParam(value = "serie", required = false) String serie
    );

    @Operation(
            summary = "Créer ou mettre à jour un bon d'achat/vente",
            description = "Si le paramètre 'serie' est fourni, on met à jour le bon correspondant. " +
                    "Sinon, on crée un nouveau bon."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bon créé ou mis à jour avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BonAchatVenteDto.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bon non trouvé (si update)", content = @Content)
    })

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping(value = APP_ROOT+"/bonvente")
    BonAchatVenteDto createBonVente(
            @RequestBody BonAchatVenteDto dto
    );


    @Operation(
            summary = "Créer ou mettre à jour un bon d'achat/vente",
            description = "Si le paramètre 'serie' est fourni, on met à jour le bon correspondant. " +
                    "Sinon, on crée un nouveau bon."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bon créé ou mis à jour avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BonAchatVenteDto.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bon non trouvé (si update)", content = @Content)
    })

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping(value = APP_ROOT+"/bonvente/update/{serie}")
    BonAchatVenteDto updateBonVente(
            @RequestBody BonAchatVenteDto dto,
            @Parameter(description = "Série du bon (obligatoire uniquement pour la mise à jour)")
            @PathVariable(value = "serie", required = false) String serie
    );

    @Operation(summary = "Récupérer un bon de vente", description = "Retourne les détails d'un bon de vente selon la série et le code utilisateur.")

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping(value = APP_ROOT + "/bonvente", produces = MediaType.APPLICATION_JSON_VALUE)
    BonAchatVenteDto getBonVente(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam(value = "userCod", required = false) Integer userCod,
            @Parameter(description = "Série du bon à rechercher")
            @RequestParam("serie") String serie
    );

    @Operation(summary = "Télécharger un bon de vente en PDF", description = "Retourne le fichier PDF du bon de vente correspondant à la série donnée.")
    @ApiResponse(
            responseCode = "200",
            description = "Fichier PDF du bon de vente",
            content = @Content(mediaType = "application/pdf")
    )

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping(value = APP_ROOT + "/bonvente/download", produces = "application/pdf")
    ResponseEntity<byte[]> downloadBonVente(

            @Parameter(description = "Série du bon à télécharger")
            @RequestParam(value = "serie", required = false) String serie
    )throws  InvalidOperationException;

    @Operation(summary = "Supprimer un bon", description = "Supprime un bon d'achat ou de vente selon sa série.")
    @ApiResponse(responseCode = "204", description = "Bon supprimé avec succès")
    @DeleteMapping(value = APP_ROOT + "/bon/delete/{serie}")
    void deleteBon(
            @Parameter(description = "Série du bon à supprimer")
            @PathVariable String serie
    );

    @Operation(summary = "Lister toutes les séries de bons d'achat", description = "Retourne la liste de toutes les séries de bons d'achat pour un utilisateur donné.")
    @ApiResponse(
            responseCode = "200",
            description = "Liste des séries de bons d'achat",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(type = "string"))
            )
    )

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping(value = APP_ROOT + "/bonsachat", produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> getAllBonsAchat(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam(value = "userCod", required = false) Integer userCod
    );

    @Operation(summary = "Lister toutes les séries de bons de vente", description = "Retourne la liste de toutes les séries de bons de vente pour un utilisateur donné.")
    @ApiResponse(
            responseCode = "200",
            description = "Liste des séries de bons de vente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(type = "string"))
            )
    )

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping(value = APP_ROOT + "/bonsvente", produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> getAllBonsVente(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam(value = "userCod", required = false) Integer userCod
    );
}
