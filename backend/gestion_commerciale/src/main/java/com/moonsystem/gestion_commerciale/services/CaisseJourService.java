package com.moonsystem.gestion_commerciale.services;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.moonsystem.gestion_commerciale.dto.CaisseJourDto;

public interface CaisseJourService {

    CaisseJourDto getCaisseJourDtos(
            Integer userCod,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    ResponseEntity<byte[]> downloadCaissePdf(Integer userCod, LocalDateTime date);
}
