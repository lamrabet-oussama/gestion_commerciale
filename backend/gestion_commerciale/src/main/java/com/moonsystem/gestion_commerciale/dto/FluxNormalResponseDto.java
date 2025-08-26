package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FluxNormalResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal stockInitial;

    private List<FluxNormalDto> fluxes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime dateInitial;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal totalEntres;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal totalSorties;

}
