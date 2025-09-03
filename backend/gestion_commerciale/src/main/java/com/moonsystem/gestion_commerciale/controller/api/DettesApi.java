package com.moonsystem.gestion_commerciale.controller.api;

import com.moonsystem.gestion_commerciale.dto.DettesDto;
import com.moonsystem.gestion_commerciale.dto.DettesResponseDto;
import com.moonsystem.gestion_commerciale.dto.TierStatistiqueCreditDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

public interface DettesApi {

    @Operation(
            summary = "Récupérer les dettes",
            description = "Retourne les dettes pour une année et une qualité donnée",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Succès - Dettes trouvées",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DettesResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Requête invalide"),
                    @ApiResponse(responseCode = "404", description = "Aucune dette trouvée")
            }
    )
    @GetMapping(value = APP_ROOT + "/dettes", produces = MediaType.APPLICATION_JSON_VALUE)
    DettesResponseDto getDettes(
            @Parameter(description = "Année concernée", example = "2024")
            @RequestParam("year") int year,

            @Parameter(description = "Qualité du tier (ex: CLIENT, FOURNISSEUR)", example = "CLIENT")
            @RequestParam("qualite") String qualite
    );

    @Operation(
            summary = "Récupérer le crédit d’un tier",
            description = "Retourne le crédit d’un tier donné",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Succès - Crédit retourné",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "number", format = "double")
                            )
                    )
            }
    )

    @GetMapping(value = APP_ROOT + "/dettes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    BigDecimal getCreditByTierId(
            @Parameter(description = "Identifiant du tier", example = "1")
            @PathVariable("id") Integer tierId
    );

    @Operation(
            summary = "Récupérer les statistiques d’un tier",
            description = "Retourne les statistiques de crédit pour un tier donné",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Succès - Statistiques trouvées",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TierStatistiqueCreditDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Tier non trouvé")
            }
    )
    @GetMapping(value = APP_ROOT + "/tier-sts/{id}/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    TierStatistiqueCreditDto getStsByTierId(
            @Parameter(description = "Identifiant du tier", example = "1")
            @PathVariable("id") Integer tierId,
            @Parameter(description = "Année de statistiques", example = "1")
            @PathVariable("year") Integer year
    );
}
