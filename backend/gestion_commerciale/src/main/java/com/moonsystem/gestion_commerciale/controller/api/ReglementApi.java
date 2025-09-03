package com.moonsystem.gestion_commerciale.controller.api;

import com.moonsystem.gestion_commerciale.dto.ReglementDto;
import com.moonsystem.gestion_commerciale.dto.ReglementResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;

@Tag(name = "Règlements", description = "Gestion des règlements")
public interface ReglementApi {

    @Operation(summary = "Créer un règlement", description = "Crée un nouveau règlement et le retourne.")
    @ApiResponse(
            responseCode = "200",
            description = "Règlement créé avec succès",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReglementDto.class))
    )
    @PostMapping(value = APP_ROOT + "/reglement/create")
    ReglementDto createReglement(@RequestBody ReglementDto dto);

    @Operation(summary = "Lister les règlements", description = "Retourne la liste des règlements pour un utilisateur et un tiers donnés.")
    @ApiResponse(
            responseCode = "200",
            description = "Liste des règlements",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReglementResponseDto.class))
    )
    @GetMapping(value = APP_ROOT + "/lister-reglements")
    ReglementResponseDto listerReglements(
            @RequestParam(required = false, name="userId") Integer userCod,
            @RequestParam(name="tierId") Integer tierId,
            @RequestParam(required = false, name="date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false, name="year") Integer year
    );
    @Operation(summary = "Mettre à jour un règlement", description = "Met à jour les informations d'un règlement existant.")
    @ApiResponse(
            responseCode = "200",
            description = "Règlement mis à jour",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReglementDto.class))
    )
    @PutMapping(value = APP_ROOT + "/reglement/update")
    ReglementDto updateReglement(@RequestBody ReglementDto dto);

    @Operation(summary = "Supprimer un règlement", description = "Supprime un règlement par son identifiant.")
    @ApiResponse(
            responseCode = "200",
            description = "Règlement supprimé avec succès"
    )
    @DeleteMapping(value = APP_ROOT + "/reglement/delete/{id}")
    void deleteReglement(@PathVariable("id") Integer id);

    @Operation(
            summary = "Télécharger un règlement en PDF",
            description = "Retourne le fichier PDF du règlement correspondant à l'utilisateur et au tier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fichier PDF du règlement",
                    content = @Content(
                            mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Règlement introuvable")
    })
    @GetMapping(value = APP_ROOT + "/reglements/download", produces = "application/pdf")
    ResponseEntity<byte[]> downloadReglementPdf(
            @Parameter(description = "Code utilisateur (optionnel)")
            @RequestParam(required = false) Integer userCod,
            @Parameter(description = "Id Tier")
            @RequestParam Integer tierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam(required = false) Integer year

    );


    @Operation(summary = "Récupérer un règlement", description = "Retourne le règlement par son id.")
    @ApiResponse(
            responseCode = "200",
            description = "Récupérer un règlement",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReglementDto.class))
    )
    @GetMapping(value = APP_ROOT + "/reglement/{idReg}")
    ReglementDto getReglement(
            @PathVariable(required = false, name="idReg") Integer idReg
    );
}
