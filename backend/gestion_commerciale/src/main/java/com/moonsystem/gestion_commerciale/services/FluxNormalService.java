package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.dto.FluxNormalDto;
import com.moonsystem.gestion_commerciale.dto.FluxNormalResponseDto;

import java.util.List;

public interface FluxNormalService {

    FluxNormalResponseDto getFluxNormalResponseArticle(Integer articleId,Integer year);

}
