package com.moonsystem.gestion_commerciale.controller.api;

import com.moonsystem.gestion_commerciale.dto.FluxNormalDto;
import com.moonsystem.gestion_commerciale.dto.FluxNormalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

public interface FluxNormalApi {
    @GetMapping(value = APP_ROOT + "/flux/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Récupérer le flux d'un article",
            description = "Retourne les informations de flux pour un article donné et pour une année spécifique. Si l'année n'est pas fournie, l'année courante sera utilisée."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Flux de l'article récupéré avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FluxNormalResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Article non trouvé",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content
            )
    })
    FluxNormalResponseDto getFluxArticle(
            @PathVariable("id") Integer articleId,
            @Parameter(
                    description = "Année pour laquelle récupérer le flux (optionnelle, défaut = année courante)",
                    required = false
            )
            @RequestParam(value = "year", required = false) Integer year
    );


}
