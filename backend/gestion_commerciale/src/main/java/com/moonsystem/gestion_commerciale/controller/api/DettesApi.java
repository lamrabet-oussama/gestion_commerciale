package com.moonsystem.gestion_commerciale.controller.api;

import com.moonsystem.gestion_commerciale.dto.DettesDto;
import com.moonsystem.gestion_commerciale.dto.DettesResponseDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

public interface DettesApi {
    @GetMapping(value=APP_ROOT+ "/dettes",produces = MediaType.APPLICATION_JSON_VALUE)
    DettesResponseDto getDettesWithTaux(@RequestParam("year") int year, @RequestParam("mvt") String mvt);
}
