package com.moonsystem.gestion_commerciale.controller.api;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.moonsystem.gestion_commerciale.dto.CaisseJourDto;
import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface CaisseJourApi {

    @Operation(
            summary = "Récupérer les données de caisse pour une journée",
            description = "Retourne les détails de la caisse pour un utilisateur donné et une période donnée."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Données récupérées avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping(value = APP_ROOT + "/caisse-jour", produces = MediaType.APPLICATION_JSON_VALUE)
     CaisseJourDto getCaisseJour(
            @Parameter(description = "Code utilisateur (optionnel)") @RequestParam(required = false) Integer userCod,
            @Parameter(description = "Date de début (format ISO 8601, ex: 2025-08-07T00:00:00)")
            @RequestParam(required = false)
             LocalDateTime startDate

    );

    @Operation(
            summary = "Télécharger le PDF de la caisse journalière",
            description = "Génère un fichier PDF contenant les détails de la caisse pour un utilisateur et une date donnés."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF généré",
                content = @Content(mediaType = "application/pdf",
                        schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "500", description = "Erreur lors de la génération du PDF")
    })
    @GetMapping(value = APP_ROOT + "/caisse-jour/download", produces = "application/pdf")
     ResponseEntity<byte[]> downloadCaisseJourPdf(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam(required = false) Integer userCod,
            @Parameter(description = "Date de début (format ISO 8601, ex: 2025-08-07T00:00:00)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate
    );

}
