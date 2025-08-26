package com.moonsystem.gestion_commerciale.controller.api;

import com.moonsystem.gestion_commerciale.dto.DettesDto;
import com.moonsystem.gestion_commerciale.dto.DettesResponseDto;
import com.moonsystem.gestion_commerciale.dto.TierStatistiqueCreditDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.moonsystem.gestion_commerciale.utils.Constants.APP_ROOT;

public interface DettesApi {
    @GetMapping(value=APP_ROOT+ "/dettes",produces = MediaType.APPLICATION_JSON_VALUE)
    DettesResponseDto getDettes(@RequestParam("year") int year,@RequestParam("qualite") String qualite);
    @GetMapping(value = APP_ROOT+"/dettes/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    List<BigDecimal>  getCreditByTierId( @PathVariable("id") Integer tierId);

    @GetMapping(value = APP_ROOT+"/tier-sts/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    TierStatistiqueCreditDto getStsByTierId(@PathVariable("id") Integer tierId);


}
