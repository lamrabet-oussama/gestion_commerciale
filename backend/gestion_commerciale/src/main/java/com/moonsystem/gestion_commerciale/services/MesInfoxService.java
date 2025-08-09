package com.moonsystem.gestion_commerciale.services;

import org.springframework.web.multipart.MultipartFile;

import com.moonsystem.gestion_commerciale.dto.MesInfoxDto;

public interface MesInfoxService {

    MesInfoxDto saveMesInfox(MultipartFile file, MesInfoxDto dto);

    MesInfoxDto findById(int id);

    MesInfoxDto findFirst();

}
