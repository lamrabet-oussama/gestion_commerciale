package com.moonsystem.gestion_commerciale.services;

import java.util.List;

import com.moonsystem.gestion_commerciale.dto.TierDto;
import com.moonsystem.gestion_commerciale.utils.PageResponse;

public interface TierService {

    TierDto save(TierDto dto);

     TierDto update(TierDto dto);

     TierDto findById(int id);
        TierDto findById(Integer id);

    boolean delete(Integer id);

    PageResponse<TierDto> search(String keyword, int page, int size);

    List<String> getAllTierType();

    List<String> getAllVillesMaroc();

}
