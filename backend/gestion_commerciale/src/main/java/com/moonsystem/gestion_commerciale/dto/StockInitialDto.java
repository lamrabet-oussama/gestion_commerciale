package com.moonsystem.gestion_commerciale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockInitialDto {
    private BigDecimal stockInitial;
    private LocalDateTime dateReference;
}
