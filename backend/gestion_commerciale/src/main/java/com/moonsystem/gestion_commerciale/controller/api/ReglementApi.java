package com.moonsystem.gestion_commerciale.controller.api;

import com.moonsystem.gestion_commerciale.dto.ReglementDto;
import com.moonsystem.gestion_commerciale.dto.ReglementResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Règlements", description = "Gestion des règlements")
public interface ReglementApi {

    @Operation(summary = "Créer un règlement", description = "Crée un nouveau règlement et le retourne.")
    @PostMapping(value = APP_ROOT + "/reglement/create")
    ReglementDto createReglement(@RequestBody ReglementDto dto);

    @Operation(summary = "Lister les règlements", description = "Retourne la liste des règlements pour un utilisateur et un tiers donnés.")
    @GetMapping(value = APP_ROOT + "/lister-reglements")
    ReglementResponseDto listerReglements(@RequestParam Integer userId, @RequestParam Integer tierId);

    @Operation(summary = "Mettre à jour un règlement", description = "Met à jour les informations d'un règlement existant.")
    @PutMapping(value = APP_ROOT + "/reglement/update")
    ReglementDto updateReglement(@RequestBody ReglementDto dto);

    @Operation(summary = "Supprimer un règlement", description = "Supprime un règlement par son identifiant.")
    @DeleteMapping(value = APP_ROOT + "/reglements/delete/{id}")
    void deleteReglement(@PathVariable("id") Integer id);
}

