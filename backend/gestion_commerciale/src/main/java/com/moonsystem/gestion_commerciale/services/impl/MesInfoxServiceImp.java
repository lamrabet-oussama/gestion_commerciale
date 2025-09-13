package com.moonsystem.gestion_commerciale.services.impl;

import java.util.List;
import java.util.Optional;

import com.moonsystem.gestion_commerciale.dto.TierDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.moonsystem.gestion_commerciale.dto.MesInfoxDto;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.model.MesInfox;
import com.moonsystem.gestion_commerciale.repository.MesInfoxRepository;
import com.moonsystem.gestion_commerciale.services.CloudinaryService;
import com.moonsystem.gestion_commerciale.services.MesInfoxService;

@Service
public class MesInfoxServiceImp implements MesInfoxService {

    private final MesInfoxRepository mesInfoxRepository;
    private final CloudinaryService cloudinaryService;

    public MesInfoxServiceImp(CloudinaryService cloudinaryService, MesInfoxRepository mesInfoxRepository) {
        this.mesInfoxRepository = mesInfoxRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public MesInfoxDto saveMesInfox(MultipartFile file, MesInfoxDto dto) {

        MesInfox entityToSave = mesInfoxRepository.findById(dto.getNum())
                .map(existing -> {
                    // Mise à jour des champs
                    existing.setNomSociete(dto.getNomSociete());
                    existing.setAdresse(dto.getAdresse());
                    existing.setActivite(dto.getActivite());



                    // Gérer l'image
                    if (file != null && !file.isEmpty()) {
                        String uploadedUrl = cloudinaryService.uploadFile(file, "mes_infos");
                        if (uploadedUrl != null) {
                            existing.setBLogo(uploadedUrl);
                        }
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    // Création d'une nouvelle entité
                    if (file != null && !file.isEmpty()) {
                        String uploadedUrl = cloudinaryService.uploadFile(file, "mes_infos");
                        if (uploadedUrl != null) {
                            dto.setBLogo(uploadedUrl);
                        }
                    }
                    return MesInfoxDto.toEntity(dto);
                });

        MesInfox saved = mesInfoxRepository.save(entityToSave);
        return MesInfoxDto.fromEntity(saved);
    }

    @Override
    public MesInfoxDto findById(int id) {
        Optional<MesInfox> existingInfos = mesInfoxRepository.findByNum((id));
            System.out.println(existingInfos);
        return existingInfos.map(MesInfoxDto::fromEntity).orElse(null);

    }


}
