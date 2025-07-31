package com.moonsystem.gestion_commerciale.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Reglement")
public class Reglement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regl")
    private Integer idRegl;

    @Column(name = "dat_regl")
    private LocalDateTime datRegl;

    @Column(name = "mouvement", length = 6)
    private String mouvement;

    @ManyToOne
    @JoinColumn(name = "tier")
    private Tier tier;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    // getters and setters
}

