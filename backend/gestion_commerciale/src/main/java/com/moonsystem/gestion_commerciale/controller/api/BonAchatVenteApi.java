package com.moonsystem.gestion_commerciale.controller.api;

import com.moonsystem.gestion_commerciale.dto.BonAchatVenteDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;
@Tag(name = "Bons Achat/Vente", description = "Gestion des bons")

public interface BonAchatVenteApi {

    @Operation(summary = "Créer un bon d'achat")
    @ApiResponse(
            responseCode = "200",
            description = "Bon d'achat créé",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BonAchatVenteDto.class))
    )
    @PostMapping(APP_ROOT + "/bonachat")
    BonAchatVenteDto createBonAchat(@RequestBody BonAchatVenteDto dto);

    @Operation(summary = "Récupérer un bon d'achat")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bon d'achat trouvé",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BonAchatVenteDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Bon non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne")
    })
    @GetMapping(value = APP_ROOT + "/bonachat", produces = MediaType.APPLICATION_JSON_VALUE)
    BonAchatVenteDto getBonAchat(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam(value = "userCod", required = false) Integer userCod,
            @Parameter(description = "Série du bon")
            @RequestParam("serie") String serie
    );

    @Operation(summary = "Télécharger un bon d'achat en PDF")
    @ApiResponse(
            responseCode = "200",
            description = "Fichier PDF",
            content = @Content(mediaType = "application/pdf")
    )
    @GetMapping(value = APP_ROOT + "/bonachat/download", produces = "application/pdf")
    ResponseEntity<byte[]> downloadBonAchat(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam Integer userCod,
            @Parameter(description = "Série du bon")
            @RequestParam String serie
    );

    @Operation(summary = "Créer un bon de vente")
    @PostMapping(APP_ROOT + "/bonvente")
    BonAchatVenteDto createBonVente(@RequestBody BonAchatVenteDto dto);

    @Operation(summary = "Récupérer un bon de vente")
    @GetMapping(value = APP_ROOT + "/bonvente", produces = MediaType.APPLICATION_JSON_VALUE)
    BonAchatVenteDto getBonVente(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam(value = "userCod", required = false) Integer userCod,
            @Parameter(description = "Série du bon")
            @RequestParam("serie") String serie
    );

    @Operation(summary = "Télécharger un bon de vente en PDF")
    @ApiResponse(
            responseCode = "200",
            description = "Fichier PDF",
            content = @Content(mediaType = "application/pdf")
    )
    @GetMapping(value = APP_ROOT + "/bonvente/download", produces = "application/pdf")
    ResponseEntity<byte[]> downloadBonVente(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam Integer userCod,
            @Parameter(description = "Série du bon")
            @RequestParam String serie
    );

    @Operation(summary = "Supprimer un bon (achat ou vente)")
    @ApiResponse(responseCode = "204", description = "Bon supprimé")
    @DeleteMapping(value = APP_ROOT + "/bon/delete/{serie}")
    void deleteBon(@Parameter(description = "Identifiant du bon") @PathVariable String serie);

    @Operation(summary = "Lister toutes les séries de bons d'achat")
    @ApiResponse(
            responseCode = "200",
            description = "Liste des séries",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(type = "string"))
            )
    )
    @GetMapping(value = APP_ROOT + "/bonsachat", produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> getAllBonsAchat(@RequestParam(value = "userCod", required = false) Integer userCod);

    @Operation(summary = "Lister toutes les séries de bons de vente")
    @ApiResponse(
            responseCode = "200",
            description = "Liste des séries",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(type = "string"))
            )
    )
    @GetMapping(value = APP_ROOT + "/bonsvente", produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> getAllBonsVente(@RequestParam(value = "userCod", required = false) Integer userCod);
}
