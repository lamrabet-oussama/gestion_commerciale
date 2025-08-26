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
        // Cherche l'entité existante par ID
        Optional<MesInfox> existingOpt = mesInfoxRepository.findById(dto.getNum());

        MesInfox entityToSave;

        if (existingOpt.isPresent()) {
            // Si l'entité existe, on met à jour ses champs manuellement
            MesInfox existing = existingOpt.get();
            existing.setNomSociete(dto.getNomSociete());
            existing.setAdresse(dto.getAdresse());
            // etc. pour tous les champs de dto vers l'entité existante

            // Gérer l'image
            if (file != null && !file.isEmpty()) {
                String uploadedUrl = cloudinaryService.uploadFile(file, "mes_infos");
                if (uploadedUrl != null) {
                    existing.setBLogo(uploadedUrl);
                }
            }

            entityToSave = existing;
        } else {
            // Sinon, c'est une nouvelle entité (création)
            if (file != null && !file.isEmpty()) {
                String uploadedUrl = cloudinaryService.uploadFile(file, "mes_infos");
                if (uploadedUrl != null) {
                    dto.setBLogo(uploadedUrl);
                }
            }
            entityToSave = MesInfoxDto.toEntity(dto);
        }

        // Sauvegarde
        MesInfox saved = mesInfoxRepository.save(entityToSave);
        return MesInfoxDto.fromEntity(saved);
    }

    @Override
    public MesInfoxDto findById(int id) {
        Optional<MesInfox> existingInfos = mesInfoxRepository.findById(id);

        if (!existingInfos.isPresent()) {
//            throw new EntityNotFoundException(
//                    "Aucune information trouvée avec l'ID : " + id,
//                    ErrorCodes.INFOX_NOT_FOUND
//            );
            return null;
        }

        return MesInfoxDto.fromEntity(existingInfos.get());

    }

    @Override
    public MesInfoxDto findFirst() {
        return this.mesInfoxRepository.findFirstInfo()
                .map(MesInfoxDto::fromEntity).orElseThrow(() -> new EntityNotFoundException(
                        "Tier not found with id: ", ErrorCodes.TIER_NOT_FOUND));
    }
}
