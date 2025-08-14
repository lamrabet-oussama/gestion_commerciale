package com.moonsystem.gestion_commerciale.controller.api;

import com.moonsystem.gestion_commerciale.dto.ArticleDto;
import com.moonsystem.gestion_commerciale.utils.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

public interface ArticleApi {

    @PostMapping(value = APP_ROOT + "/articles/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Créer un nouvel article",
            description = "Permet de créer un nouvel article dans le système de gestion commerciale"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Article créé avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ArticleDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Données d'entrée invalides",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Article avec ce code existe déjà",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    ArticleDto save(
            @Parameter(description = "Données de l'article à créer", required = true)
            @RequestBody ArticleDto dto
    );

    @GetMapping(value = APP_ROOT + "/articles/{codeArticle}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Récupérer un article par son code",
            description = "Permet de récupérer les détails d'un article spécifique en utilisant son code unique"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Article trouvé avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ArticleDto.class)
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
    ArticleDto findByCod(
            @Parameter(description = "Code unique de l'article", required = true, example = "1001")
            @PathVariable("codeArticle") Integer code
    );

    @GetMapping(value = APP_ROOT + "/articles/all",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Récupérer tous les articles",
            description = "Retourne la liste complète de tous les articles disponibles dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Liste des articles récupérée avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ArticleDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    List<ArticleDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/articles/delete/{codeArticle}")
    @Operation(
            summary = "Supprimer un article",
            description = "Permet de supprimer définitivement un article du système en utilisant son code"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Article supprimé avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = Boolean.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Article non trouvé",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Impossible de supprimer l'article (contraintes référentielles)",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    boolean delete(
            @Parameter(description = "Code unique de l'article à supprimer", required = true, example = "1001")
            @PathVariable("codeArticle") Integer code
    );

    @PutMapping(value = APP_ROOT + "/articles/update/{codeArticle}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Mettre à jour un article",
            description = "Permet de mettre à jour complètement les informations d'un article existant"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Article mis à jour avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ArticleDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Données d'entrée invalides",
                content = @Content
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
    ArticleDto update(
            @Parameter(description = "Code unique de l'article à mettre à jour", required = true, example = "1001")
            @PathVariable("codeArticle") Integer code,
            @Parameter(description = "Nouvelles données de l'article", required = true)
            @RequestBody ArticleDto dto
    );

    @GetMapping(value = APP_ROOT + "/articles/familles",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Lister les familles d'article disponibles",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Liste des familles",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                array = @ArraySchema(schema = @Schema(implementation = String.class))
                        )
                )
            }
    )
    List<String> getFamilles();

    @GetMapping(value = APP_ROOT + "/articles/choix",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Lister les choix d'article disponibles",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Liste des choix",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                array = @ArraySchema(schema = @Schema(implementation = String.class))
                        )
                )
            }
    )
    List<String> getChoix();

    @GetMapping(value = APP_ROOT + "/articles/search",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Rechercher des articles",
            description = "Permet de rechercher des articles en utilisant un mot-clé qui sera appliqué sur différents champs (nom, description, code, etc.)"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Résultats de recherche récupérés avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = @ArraySchema(schema = @Schema(implementation = ArticleDto.class))
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
    List<ArticleDto> searchArticles(
            @Parameter(description = "Mot-clé pour la recherche", required = true, example = "ordinateur")
            @RequestParam String keyword
    );

    @PatchMapping(value = APP_ROOT + "/articles/stock/{codeArticle}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Mettre à jour le stock d'un article",
            description = "Permet de mettre à jour uniquement la quantité en stock d'un article spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Stock mis à jour avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ArticleDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Quantité de stock invalide",
                content = @Content
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
    ArticleDto updateStock(
            @Parameter(description = "Code unique de l'article", required = true, example = "1001")
            @PathVariable("codeArticle") Integer cod,
            @Parameter(description = "Nouvelle quantité en stock", required = true, example = "150.50")
            @RequestBody BigDecimal newStock
    );

    @GetMapping(value = APP_ROOT + "/articles",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Récupérer les articles avec pagination",
            description = "Retourne une liste paginée des articles avec la possibilité de spécifier la page et la taille de la page"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Articles paginés récupérés avec succès",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = PageResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Paramètres de pagination invalides",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
    })
    PageResponse<ArticleDto> getArticlesPaginated(
            @Parameter(description = "Numéro de la page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size
    );

    @GetMapping(value = APP_ROOT + "/totalelements")
    @Operation(
            summary = "Obtenir le nombre total d'éléments",
            description = "Retourne le nombre total d'éléments présents dans la base de données."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nombre total d'éléments retourné avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    Integer getTotalElements();

}
