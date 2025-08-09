package com.moonsystem.gestion_commerciale.controller.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.moonsystem.gestion_commerciale.dto.ArticleDto;
import com.moonsystem.gestion_commerciale.dto.MesInfoxDto;
import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface MesInfosApi {

    @PostMapping(
            value = APP_ROOT + "/mes-infos/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Créer ou mettre à jour les informations personnelles",
            description = "Permet de créer une nouvelle fiche avec une image. Si une image existe déjà, elle sera supprimée et remplacée."
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Fiche créée ou mise à jour avec succès",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = MesInfoxDto.class))
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Requête invalide",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })

    public MesInfoxDto update(
            @RequestPart("mesInfos") MesInfoxDto mesInfos,
            MultipartFile file);

    @GetMapping(value = APP_ROOT + "/mes-infos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Récupérer les informations de l'entreprise",
            description = "Permet de récupérer les informations de l'entreprise par son ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Article trouvé avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = MesInfoxDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Article non trouvé pour l'ID fourni",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    public MesInfoxDto findById(@PathVariable("id") Integer id);
}
