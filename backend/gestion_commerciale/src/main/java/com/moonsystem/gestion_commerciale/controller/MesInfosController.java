package com.moonsystem.gestion_commerciale.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moonsystem.gestion_commerciale.controller.api.MesInfosApi;
import com.moonsystem.gestion_commerciale.dto.MesInfoxDto;
import com.moonsystem.gestion_commerciale.services.MesInfoxService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "MesInfos", description = "API de gestion des infos de l'entreprise")
public class MesInfosController implements MesInfosApi {

    private final MesInfoxService mesInfoxService;

    public MesInfosController(MesInfoxService mesInfoxService) {
        this.mesInfoxService = mesInfoxService;
    }

    @Override
    public MesInfoxDto update(MesInfoxDto mesInfos, MultipartFile file) {
        // Implementation of the update method
        return this.mesInfoxService.saveMesInfox(file, mesInfos);
    }

    @Override
    public MesInfoxDto findById(Integer id) {
        return this.mesInfoxService.findById(id);
    }
}
